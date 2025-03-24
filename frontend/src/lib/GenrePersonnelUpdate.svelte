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

    export let type;
    export let maxlength;

    let selectedName = "";
    let apiName;
    let fieldName;
    let showEditField;
    let changeName;
    let selectedId;
    let modalOpen = false;

    async function changeHandler() {
        if (!changeName || changeName.trim() === "") {
            showEditField = false;
            return;
        }
        changeName = changeName.trim();

        const payload = {
            [fieldName]: changeName
        };

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
                            message: "Entry already exists",
                            type: "error",
                        });     
                    } else if(text.includes("Not found")) {
                        addToast({
                            message: "Entry not found",
                            type: "error",
                        }); 
                    }
                    else {
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
                    message: "Entry updated successfully",
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
            changeName = "";
            selectedName = "";
            showEditField = false;
        });
    }

    function deleteHandler() {
        modalOpen = true;
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
                            message: "Entry not found",
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
        })
        .then(result => {
           if (result !== null) {
                addToast({
                    message: "Entry deleted successfully",
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
            selectedName = "";   
            selectedName = "";
            showEditField = false;
        });
    }
    
    function selectHandler(event) {
        const { id, name } = event.detail;
        selectedName = name;
        selectedId = id;
        showEditField = true;
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
    <h1>Update {type} Info</h1>
    <p>Update {type} info by selecting an existing entry from the list</p>
    <Form class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
        <FormGroup class="mb-4">
        <SearchField 
            placeholder="Enter {type} name" 
            maxlength={maxlength}
            bind:value={selectedName}
            on:select={selectHandler}
            on:fieldFocused={() => { showEditField = false; }}
            searchEndpoint={`/api/public/search-${apiName}`}
        />
        </FormGroup>
        {#if showEditField}
            <Input 
                class="mb-4"
                maxlength={maxlength}
                type="text"
                bind:value={changeName}
                placeholder="Enter a new name"
            />
            <Container class="d-flex justify-content-between">
                <Button color="success" on:click={changeHandler}>Change</Button>
                <Button color="danger" on:click={deleteHandler}>Delete</Button>
            </Container>
        {/if}
    </Form>
</Container>
{#if modalOpen}
    <Modal 
        modalTitle={"Are you sure you want to delete " + selectedName + "?"}
        modalBody={"This will remove " + selectedName + " from every associated movie"}
        buttonText="Delete"
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
