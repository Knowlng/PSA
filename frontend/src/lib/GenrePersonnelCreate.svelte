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

    export let type;
    export let maxlength;

    let apiName;
    let fieldName;
    let newName = "";

    async function submitHandler() {
        newName = newName.trim();
        if (!newName) {
            return;
        }

        const payload = {
            [fieldName]: newName
        };

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
                            message: "Entry already exists",
                            type: "error",
                        });                        
                    } else {
                       addToast({
                            message: "Something went wrong. Please try again later.",
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
                    message: "Entry created successfully",
                    type: "success",
                });
            }
        })
        .catch(error => {
            addToast({
                message: "Something went wrong. Please try again later.",
                type: "error",
            });
        })
        .finally(() => {
            newName = "";
        });
    }


    onMount(() => {
        if(type === "Genre") {
            fieldName = "genreName";
            apiName = "genre";
        } else if(type === "Personnel") {
            fieldName = "personFullName";
            apiName = "person";
        }
    });
</script>


<Container>
    <h1>Create {type}</h1>
    <p class="text-center">Create a {type} by writting the name below</p>
    <Form class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
        <Input 
            class="mb-4"
            maxlength={maxlength}
            type="text"
            bind:value={newName}
        />
        <Container class="d-flex justify-content-center">
            <Button color="primary" on:click={submitHandler}>Submit</Button>
        </Container>
    </Form>
</Container>

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
</style>
