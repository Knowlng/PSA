<script>
    import { onMount } from 'svelte';
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
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { USER_NAME_LENGTH } from '$lib/consts.js';
    import Modal from '$lib/Modal.svelte';
    import SearchField from '$lib/SearchField.svelte';
    import { _ } from "svelte-i18n";

    let selectedName;
    let selectedId;
    let userDetails;

    let showEditField = false;
    let modalOpen = false;

    async function getUserInfo() {
        try {
            const response = await fetch(`/api/admin/user/${selectedId}`);
            if (!response.ok) {
                const text = response.text();
                if(text.includes("User not found")) {
                    addToast({
                        message: $_("ErrorMessages.userNotFound"),
                        type: "error",
                    });
                } else {
                    addToast({
                        message: $_("ErrorMessages.somethingWentWrong"),
                        type: "error",
                    });
                }
            }

            userDetails = await response.json();
        } catch (error) {
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        }
    }

    function confirmHandler() {
        let action = userDetails.enabled ? 'disable' : 'enable';

        fetch(`/api/admin/${action}-user/${selectedId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("User not found")) {
                        addToast({
                            message: $_("ErrorMessages.userNotFound"),
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
            return response;
        })
        .then(result => {
            if (result !== null) {
                if(action === 'disable') {
                    addToast({
                        message: $_("ErrorMessages.userDisabledSuccessfully"),
                        type: "success",
                    });
                } else if (action === 'enable') {
                    addToast({
                        message: $_("ErrorMessages.userEnabledSuccessfully"),
                        type: "success",
                    });
                }
                userDetails.enabled = !userDetails.enabled;
            }
        })
        .catch(error => {
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        });
    }

    async function selectHandler(event) {
        const { id, name } = event.detail;
        selectedName = name;
        selectedId = id;
        await getUserInfo();
        showEditField = true;
    }

    onMount(async () => {
        const urlParams = new URLSearchParams(window.location.search);
        const userId = urlParams.get('userId');
        const userName = urlParams.get('username');
        if (userId) {
            selectedId = userId;
            selectedName = userName;
            await getUserInfo();
            showEditField = true;
        }
    });

</script>


<Container>
    <h1>{$_("ManageUsers.manageUsers")}</h1>
    <p class="text-center">{$_("ManageUsers.updateUserText")}</p>
    <Form class="w-75 mx-auto" style="min-width: 100px; max-width: 700px;">
        <SearchField 
            placeholder={$_("ManageUsers.enterUserName")}
            maxlength={USER_NAME_LENGTH}
            bind:value={selectedName}
            on:select={selectHandler}
            on:fieldFocused={() => { showEditField = false; selectedName=''; }}
            searchEndpoint="/api/admin/search-user"
        />
        {#if showEditField}
        <Container class="mt-3 d-flex justify-content-center align-items-center">
            <h4>{$_("ManageUsers.userInformation")}</h4>
        </Container>
           <Container class="d-flex justify-content-between mt-3 align-items-center user-entry">
                <p>{$_("ManageUsers.userName")}<strong>{userDetails.userName}</strong></p>
                <p>{$_("ManageUsers.role")}<strong>{userDetails.userRole}</strong></p>
                <p>{$_("ManageUsers.account")}<strong class={userDetails.enabled ? 'enabled' : 'disabled'}>{userDetails.enabled ? $_("ManageUsers.enabled") : $_("ManageUsers.disabled")}</strong></p>
                <Button class="mb-3"color="primary" on:click={()=> {modalOpen = true }}>{userDetails.enabled ? $_("ManageUsers.disable") : $_("ManageUsers.enable")}</Button>
           </Container>
        {/if}
    </Form>
</Container>
{#if modalOpen}
    <Modal 
        modalTitle={$_("ManageUsers.areYouSureYouWantToDisable") + selectedName + "?"}
        modalBody={$_("ManageUsers.thisWillNotAllow") + selectedName + $_("ManageUsers.toLoginToTheSiteAnymore")}
        buttonText={userDetails.enabled ? $_("ManageUsers.disable") : $_("ManageUsers.enable")}
        buttonColor={userDetails.enabled ? 'danger' : 'success'}
        on:toggle={() => { modalOpen = false; }}
        on:confirm={confirmHandler}
    />
{/if}

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
    .enabled {
        color: var(--success);
    }
    .disabled {
        color: var(--danger);
    }

    @media (max-width: 500px) {
        :global(.user-entry) {
        flex-direction: column;
        }
    }
</style>
