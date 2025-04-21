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
    import { ListGroup, ListGroupItem } from '@sveltestrap/sveltestrap';
    import SearchField from '$lib/SearchField.svelte'; 
    import { onMount } from 'svelte';
    import Modal from '$lib/Modal.svelte';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { _ } from "svelte-i18n";

    export let type;
    export let maxlength;

    let selectedName = "";
    let apiName;
    let showEditField;
    let changeNameEN = "";
    let changeNameLT = "";
    let selectedId;
    let modalOpen = false;

    async function changeHandler() {
        let payload;
        changeNameEN = changeNameEN.trim();
        changeNameLT = changeNameLT.trim();
        if (!changeNameEN || !changeNameLT) {
            addToast({
                message: $_("ErrorMessages.pleaseFillInAllFields"),
                type: "error",
            });
            return;
        }

        if(type === "Genre") {
            payload = {
                enGenreName: changeNameEN,
                ltGenreName: changeNameLT
            };
        } else if(type === "Personnel") {
            payload = {
                enPersonName: changeNameEN,
                ltPersonName: changeNameLT
            };        
        }

        fetch(`/api/admin/update-${apiName}/${selectedId}`, {
            method: 'PUT',
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
                    } else if(text.includes("Not found")) {
                        addToast({
                            message: $_("ErrorMessages.entryNotFound"),
                            type: "error",
                        }); 
                    }
                    else {
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
                    message: $_("ErrorMessages.entryUpdatedSuccessfully"),
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
            changeNameEN = "";
            changeNameLT = "";
            selectedName = "";
            showEditField = false;
        });
    }

    function confirmDelete() {
        fetch(`/api/admin/delete-${apiName}/${selectedId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("Not found")) {
                        addToast({
                            message: $_("ErrorMessages.entryNotFound"),
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
        })
        .then(result => {
           if (result !== null) {
                addToast({
                    message: $_("ErrorMessages.entryDeletedSuccessfully"),
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
            selectedName = "";   
            showEditField = false;
        });
    }
    
    async function selectHandler(event) {
        const { id, name } = event.detail;
        selectedName = name;
        selectedId = id;
        await fetchTranslations();
        showEditField = true;
    }

    async function fetchTranslations() {

        fetch(`/api/admin/${apiName}/${selectedId}/translations`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes(`Not found`)) {
                        if(type === "Genre") {
                            addToast({
                                message: $_("ErrorMessages.genreNotFound"),
                                type: "error",
                            }); 
                        } else if(type === "Personnel") {
                            addToast({
                                message: $_("ErrorMessages.personnelNotFound"),
                                type: "error",
                            }); 
                        }                   
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
        .then(data => {
            changeNameEN = data.en;
            changeNameLT = data.lt;
        })
        .catch(error => {
            console.log(error);
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        })
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
   <h1>{$_("GenrePersonnelUpdate.update")} {type === "Genre" ?  $_("GenrePersonnelUpdate.genres") : $_("GenrePersonnelUpdate.personnels")} {$_("GenrePersonnelUpdate.info")}</h1>
    <p>{$_("GenrePersonnelUpdate.updateIt")} {type === "Genre" ?  $_("GenrePersonnelUpdate.genres") : $_("GenrePersonnelUpdate.personnels")} {$_("GenrePersonnelUpdate.updateItSecondHalf")} </p>
    <Form class="w-75 mx-auto" style="min-width: 100px; max-width: 700px;">
        <FormGroup class="mb-4">
        <SearchField 
            placeholder={$_("GenrePersonnelUpdate.enter") + (type === "Genre" ? $_("GenrePersonnelUpdate.genres") : $_("GenrePersonnelUpdate.personnels")) + (type === "Genre" ? $_("GenrePersonnelUpdate.name") : $_("GenrePersonnelUpdate.fullName"))}
            maxlength={maxlength}
            bind:value={selectedName}
            on:select={selectHandler}
            on:fieldFocused={() => { showEditField = false; selectedName = ""; }}
            searchEndpoint={`/api/public/search-${apiName}`}
        />
        </FormGroup>
        {#if showEditField}
            <Label>{$_("GenrePersonnelUpdate.enterANew")} {type === "Genre" ?  $_("GenrePersonnelUpdate.genres") : $_("GenrePersonnelUpdate.personnels")} {type === "Genre" ?  $_("GenrePersonnelUpdate.name") : $_("GenrePersonnelUpdate.fullName")} {$_("GenrePersonnelUpdate.inEnglish")}</Label>
            <Input 
                class="mb-4"
                maxlength={maxlength}
                type="text"
                bind:value={changeNameEN}
            />
            <Label>{$_("GenrePersonnelUpdate.enterANew")} {type === "Genre" ?  $_("GenrePersonnelUpdate.genres") : $_("GenrePersonnelUpdate.personnels")} {type === "Genre" ?  $_("GenrePersonnelUpdate.name") : $_("GenrePersonnelUpdate.fullName")} {$_("GenrePersonnelUpdate.inLithuanian")}</Label>
            <Input 
                class="mb-4"
                maxlength={maxlength}
                type="text"
                bind:value={changeNameLT}
            />
            <Container class="d-flex justify-content-between">
                <Button color="success" on:click={changeHandler}>{$_("GenrePersonnelUpdate.change")}</Button>
                <Button color="danger" on:click={() => modalOpen = true}>{$_("GenrePersonnelUpdate.delete")}</Button>
            </Container>
        {/if}
    </Form>
</Container>
{#if modalOpen}
    <Modal 
        modalTitle={$_("GenrePersonnelUpdate.areYouSure") + selectedName + "?"}
        modalBody={$_("GenrePersonnelUpdate.thisWillRemove") + selectedName + $_("GenrePersonnelUpdate.thisWillRemoveSecondHalf")}
        buttonText={$_("GenrePersonnelUpdate.delete")}
        on:toggle={() => { modalOpen = false; }}
        on:confirm={confirmDelete}
    />
{/if}

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
    p {
        text-align: center;
    }
</style>
