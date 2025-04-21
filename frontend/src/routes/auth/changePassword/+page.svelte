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
    import { _ } from "svelte-i18n";

    let password = '';
    let repeatedPass = '';
    let deletePassInput = '';
    let username;

    let changeModalOpen = false;
    let deleteAccountModalOpen = false;

    function changeHandler() {
        if(password.trim() === '') {
            return;
        }
        if (!PASS_REGEX.test(password)) {
            addToast({
                message: $_("ErrorMessages.passwordDoesNotMeetTheRequirements"),
                type: "error",
            });   
            return;
        }
        if (password !== repeatedPass) {
            addToast({
                message: $_("ErrorMessages.passwordsDoNotMatch"),
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
                            message: $_("ErrorMessages.notLoggedIn"),
                            type: "error",
                        });
                    } else if (text.includes("User not found")) {
                        addToast({
                            message: $_("ErrorMessages.userNotFound"),
                            type: "error",
                        });
                    } else if (text.includes("Passwords do not match")) {
                        addToast({
                            message: $_("ErrorMessages.passwordsDoNotMatch"),
                            type: "error",
                        });
                    } else if (text.includes("New password cannot be the same as the old password")) {
                        addToast({
                            message: $_("ErrorMessages.newPasswordCannotBeTheSameAsOldPassword"),
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
            return response.text();
        })
        .then(result => {
            if (result !== null) {
                addToast({
                    message: $_("ErrorMessages.passwordChangedSuccessfully"),
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
            password = "";
            repeatedPass = "";
        });
    }


    async function confirmDelete() {
        if (deletePassInput.trim() === '') {
            addToast({
                message: $_("ErrorMessages.pleaseEnterYourPassword"),
                type: "error",
            });
            return;
        }

        try {
            const response = await fetch(`/api/auth/delete-account`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password: deletePassInput.trim() })
            });

            if (!response.ok) {
                const text = await response.text();
                if (text.includes("Not logged in")) {
                    addToast({
                        message: $_("ErrorMessages.notLoggedIn"),
                        type: "error",
                    });
                } else if (text.includes("User not found")) {
                    addToast({
                        message: $_("ErrorMessages.userNotFound"),
                        type: "error",
                    });
                } else if (text.includes("Invalid password")) {
                    addToast({
                        message: $_("ErrorMessages.invalidPassword"),
                        type: "error",
                    });
                } else {
                    addToast({
                        message: $_("ErrorMessages.somethingWentWrong"),
                        type: "error",
                    });
                }
                return;
            }

            const result = await response.text();
            if (result !== null) {
                localStorage.clear();
                window.location.href = '/';
                addToast({
                    message: $_("ErrorMessages.accountDeletedSuccessfully"),
                    type: "success",
                });
            }
        } catch (error) {
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        }
    }


    onMount(() => {
        username = localStorage.getItem('username');
    });

</script>
<Container class="pt-5">
    <h1 class="text-center mb-5">{$_("ChangePassword.changePassword")}</h1>
    <p class="text-center">{$_("LoginRegister.pleaseProvideAPasswordThatContains:")}</p>
    <ul class="text-center" style="list-style-type: none;">
        <li>{$_("LoginRegister.atLeast")}<strong> 8 {$_("LoginRegister.characters")}</strong></li>
        <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.uppercaseLetter")}</strong></li>
        <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.number")}</strong></li>
        <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.specialCharacter")}</strong></li>
    </ul>
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>{$_("ChangePassword.newPassword")}</Label>
            <Input bind:value={password} maxlength={USER_PASS_LENGTH} required placeholder={$_("ChangePassword.enterNewPassword")} />
        </FormGroup>
        <FormGroup>
            <Label>{$_("ChangePassword.repeatNewPassword")}</Label>
            <Input bind:value={repeatedPass} maxlength={USER_PASS_LENGTH} required placeholder={$_("ChangePassword.enterRepeatNewPassword")} />
        </FormGroup>
        <Container class="text-center p-0 mb-4">
            <Button color="primary" on:click={changeHandler}>
                {$_("ChangePassword.change")}
            </Button>
        </Container>
    </Form>
    <Container class="d-flex justify-content-end w-25">
        <button on:click={() => { deleteAccountModalOpen = true; }} class="delete-account">{$_("ChangePassword.deleteAccount")}</button>
    </Container>
</Container>
{#if changeModalOpen}
    <Modal 
        modalTitle={$_("ChangePassword.changePassword")}
        modalBody={$_("ChangePassword.thisActionCannotBeUndone")}
        buttonText={$_("ChangePassword.change")}
        on:toggle={() => { changeModalOpen = false; }}
        on:confirm={confirmChange}
    />
{/if}
{#if deleteAccountModalOpen}
    <Modal 
        modalTitle={$_("ChangePassword.areYouSureYouWantToDeleteYourAccount")}
        modalBody={$_("ChangePassword.thisActionCannotBeUndone")}
        buttonText={$_("ChangePassword.deleteAccountButton")}
        on:toggle={() => { deleteAccountModalOpen = false; deletePassInput = ''; }}
        on:confirm={confirmDelete}
    >
        <Form> 
            <FormGroup class="text-center">
                <Label>{$_("ChangePassword.typeInYour")}<strong>{$_("ChangePassword.password")}</strong>{$_("ChangePassword.toConfirm")}</Label>
                <Input bind:value={deletePassInput} maxlength={USER_PASS_LENGTH} placeholder={$_("ChangePassword.inputPassword")} type="password"/>
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