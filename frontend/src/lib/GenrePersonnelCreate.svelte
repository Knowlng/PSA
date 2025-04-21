<script>
    import {
        Badge,
        Form,
        Input,
        FormGroup,
        InputGroup,
        InputGroupText,
        Label,
        Container,
        Button
    } from '@sveltestrap/sveltestrap'; 
    import { onMount } from 'svelte';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { _ } from "svelte-i18n";

    export let type;
    export let maxlength;

    let apiName;
    let newNameEN = "";
    let newNameLT = "";

    async function submitHandler() {
        let payload;
        newNameEN = newNameEN.trim();
        newNameLT = newNameLT.trim();
        if (!newNameEN || !newNameLT) {
            addToast({
                message: $_("ErrorMessages.pleaseFillInAllFields"),
                type: "error",
            });
            return;
        }

        if(type === "Genre") {
            payload = {
                enGenreName: newNameEN,
                ltGenreName: newNameLT
            };
        } else if(type === "Personnel") {
            payload = {
                enPersonName: newNameEN,
                ltPersonName: newNameLT
            };        
        }

        fetch(`/api/admin/create-${apiName}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload),
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("Entry already exists")) {
                        addToast({
                            message: $_("ErrorMessages.entryAlreadyExists"),
                            type: "error",
                        });
                    } else if (text.includes("English entry already exists")) {
                        addToast({
                            message: $_("ErrorMessages.englishEntryAlreadyExists"),
                            type: "error",
                        });
                    } else {
                       addToast({
                            message: $_("ErrorMessages.somethingWentWrong"),
                            type: "error",
                        });
                    }
                    return null;
                });
            }
            return response.json();
        })
        .then(result => {
            if (result !== null) {
                addToast({
                    message: $_("ErrorMessages.entryCreatedSuccessfully"),
                    type: "success",
                });
            }
        })
        .catch(error => {
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        })
        .finally(() => {
            newNameEN = "";
            newNameLT = "";
        });
    }

    onMount(() => {
        if(type === "Genre") {
            apiName = "genre";
        } else if(type === "Personnel") {
            apiName = "person";
        }
    });
</script>


<Container>
    <h1>{$_("GenrePersonnelCreate.create")} {type === "Genre" ?  $_("GenrePersonnelCreate.genre") : $_("GenrePersonnelCreate.personnel")}</h1>
    <p class="text-center">{$_("GenrePersonnelCreate.createFirstHalf")} {type === "Genre" ?  $_("GenrePersonnelCreate.genre") : $_("GenrePersonnelCreate.personnel")} {$_("GenrePersonnelCreate.createSecondHalf")}</p>
    <Form class="w-75 mx-auto" style="min-width: 100px; max-width: 700px;">
        <Label>
            {type === "Genre" ?  $_("GenrePersonnelCreate.genres") : $_("GenrePersonnelCreate.personnels")}
            {type === "Genre" ?  $_("GenrePersonnelCreate.name") : $_("GenrePersonnelCreate.fullName")}
            {$_("GenrePersonnelCreate.inEnglish")}
        </Label>
        <Input 
            class="mb-4"
            maxlength={maxlength}
            type="text"
            bind:value={newNameEN}
        />
        <Label>
            {type === "Genre" ?  $_("GenrePersonnelCreate.genres") : $_("GenrePersonnelCreate.personnels")}
            {type === "Genre" ?  $_("GenrePersonnelCreate.name") : $_("GenrePersonnelCreate.fullName")}
            {$_("GenrePersonnelCreate.inLithuanian")}
        </Label>
        <Input 
            class="mb-4"
            maxlength={maxlength}
            type="text"
            bind:value={newNameLT}
        />
        <Container class="d-flex justify-content-center">
            <Button color="primary" on:click={submitHandler}>{$_("GenrePersonnelCreate.submit")}</Button>
        </Container>
    </Form>
</Container>

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
</style>
