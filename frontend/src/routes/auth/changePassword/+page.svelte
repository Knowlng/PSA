<script>
    import {
        Container,
        Button,
        FormGroup,
        Label,
        Input,
        Form
    } from '@sveltestrap/sveltestrap';
    import Modal from '$lib/Modal.svelte';
    import { PASS_REGEX, USER_PASS_LENGTH, USER_NAME_LENGTH } from '$lib/consts.js';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { onMount } from 'svelte';

    let password = '';
    let repeatedPass = '';
    let usernameInput = '';
    let username;

    let changeModalOpen = false;
    let deleteAccountModalOpen = false;

    function changeHandler() {
        if(password.trim() === '') {
            return;
        }
        if (!PASS_REGEX.test(password)) {
            addToast({
                message: "Password does not meet the requirements",
                type: "error",
            });   
            return;
        }
        if (password !== repeatedPass) {
            addToast({
                message: "Passwords do not match",
                type: "error",
            });  
            return;
        }
        changeModalOpen = true;
    }
    
    function confirmChange() {
        const payload = {
            newPassword: password,
            repeatedPassword: repeatedPass
        };

        fetch(`/api/auth/change-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("Not logged in")) {
                        addToast({
                            message: "Not logged in",
                            type: "error",
                        });
                    } else if (text.includes("User not found")) {
                        addToast({
                            message: "User not found",
                            type: "error",
                        });
                    } else if (text.includes("Passwords do not match")) {
                        addToast({
                            message: "Passwords do not match",
                            type: "error",
                        });
                    } else if (text.includes("New password cannot be the same as the old password")) {
                        addToast({
                            message: "New password cannot be the same as the old password",
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
            return response.text();
        })
        .then(result => {
            if (result !== null) {
                addToast({
                    message: "Password changed successfully",
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
            password = "";
            repeatedPass = "";
        });
    }


    function confirmDelete() {
        if(usernameInput.trim() === '') {
            addToast({
                message: "Please enter your username",
                type: "error",
            });   
            return;
        } else if(usernameInput !== localStorage.getItem('username')) {
            addToast({
                message: "Username does not match",
                type: "error",
            });   
            return;
        }

        fetch(`/api/auth/delete-account`, {
            method: 'POST',
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("Not logged in")) {
                        addToast({
                            message: "Not logged in",
                            type: "error",
                        });
                    } else if (text.includes("User not found")) {
                        addToast({
                            message: "User not found",
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
            return response.text();
        })
        .then(result => {
            if (result !== null) {
                localStorage.clear();
                window.location.href = '/';
                addToast({
                    message: "Account deleted successfully",
                    type: "success",
                });
            }
        })
        .catch(error => {
            addToast({
                message: "Something went wrong. Please try again later.",
                type: "error",
            });
        });

    }

    onMount(() => {
        username = localStorage.getItem('username');
    });

</script>
<Container class="pt-5">
    <h1 class="text-center mb-5">Change Password</h1>
    <p class="text-center">Please provide a password that contains:</p>
    <ul class="text-center" style="list-style-type: none;">
        <li>At least <strong>8 characters</strong></li>
        <li>At least one <strong>uppercase letter</strong></li>
        <li>At least one <strong>number</strong></li>
        <li>At least one <strong>special character</strong></li>
    </ul>
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>New Password</Label>
            <Input bind:value={password} maxlength={USER_PASS_LENGTH} required placeholder="Enter new password" />
        </FormGroup>
        <FormGroup>
            <Label>Repeat new password</Label>
            <Input bind:value={repeatedPass} maxlength={USER_PASS_LENGTH} required placeholder="Repeat new password" />
        </FormGroup>
        <Container class="text-center p-0 mb-4">
            <Button color="primary" on:click={changeHandler}>
                Change
            </Button>
        </Container>
    </Form>
    <Container class="d-flex justify-content-end w-25">
        <button on:click={() => { deleteAccountModalOpen = true; }} class="delete-account">Delete account?</button>
    </Container>
</Container>
{#if changeModalOpen}
    <Modal 
        modalTitle={"Change password?"}
        modalBody={"This action can not be undone!"}
        buttonText="Change"
        on:toggle={() => { changeModalOpen = false; }}
        on:confirm={confirmChange}
    />
{/if}
{#if deleteAccountModalOpen}
    <Modal 
        modalTitle={"Are you sure you want to delete your account?"}
        modalBody={"This action cannot be undone!"}
        buttonText="Delete"
        on:toggle={() => { deleteAccountModalOpen = false; usernameInput = ''; }}
        on:confirm={confirmDelete}
    >
        <Form> 
            <FormGroup class="text-center">
                <Label>Type in <strong>{username}</strong> to delete your account</Label>
                <Input bind:value={usernameInput} maxlength={USER_NAME_LENGTH} placeholder="username" />
            </FormGroup>
        </Form>
    </Modal>
{/if}
<style>
    .delete-account {
        text-decoration: underline;
        font-weight: bold;
        color: var(--danger);
        border:none;
        background-color: transparent;
    }
</style>