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
                message: "Please fill in all fields",
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
                                message: "Genre not found",
                                type: "error",
                            }); 
                        } else if(type === "Personnel") {
                            addToast({
                                message: "Personnel not found",
                                type: "error",
                            }); 
                        }                   
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
        .then(data => {
            changeNameEN = data.en;
            changeNameLT = data.lt;
        })
        .catch(error => {
            addToast({
                message: "Something went wrong. Please try again later.",
                type: "error",
            });
        })
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
    <Form class="w-75 mx-auto" style="min-width: 100px; max-width: 700px;">
        <FormGroup class="mb-4">
        <SearchField 
            placeholder="Enter {type} name" 
            maxlength={maxlength}
            bind:value={selectedName}
            on:select={selectHandler}
            on:fieldFocused={() => { showEditField = false; selectedName = ""; }}
            searchEndpoint={`/api/public/search-${apiName}`}
        />
        </FormGroup>
        {#if showEditField}
            <Label>Enter English {type} name</Label>
            <Input 
                class="mb-4"
                maxlength={maxlength}
                type="text"
                bind:value={changeNameEN}
                placeholder="Enter new {type} name in English"
            />
            <Label>Enter Lithuanian {type} name</Label>
            <Input 
                class="mb-4"
                maxlength={maxlength}
                type="text"
                bind:value={changeNameLT}
                placeholder="Enter new {type} name in Lithuanian"
            />
            <Container class="d-flex justify-content-between">
                <Button color="success" on:click={changeHandler}>Change</Button>
                <Button color="danger" on:click={() => modalOpen = true}>Delete</Button>
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
