<script>
    import {
        Container,
        Button,
        FormGroup,
        Label,
        Input,
        Form
    } from '@sveltestrap/sveltestrap';
    import { USER_NAME_LENGTH } from '$lib/consts.js';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { onMount } from 'svelte';
    import { _ } from "svelte-i18n";

    let username = '';
    let currentUsername = '';
    
    function handlechange() {
        username = username.toLowerCase();
        if(username.trim() === '') {
            return;
        } else if(username === currentUsername) {
            addToast({
                message: $_("ErrorMessages.newUsernameCannotBeTheSameAsOldUsername"),
                type: "error",
            });  
        }
        
        const payload = { newUsername: username };

        fetch(`/api/auth/change-username`, {
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
                    } else if (text.includes("Usernames cannot be the same")) {
                        addToast({
                            message: $_("ErrorMessages.newUsernameCannotBeTheSameAsOldUsername"),
                            type: "error",
                        });
                    } else if (text.includes("Username is already taken")) {
                        addToast({
                            message: $_("ErrorMessages.usernameIsAlreadyTaken"),
                            type: "error",
                        });
                    } else if (text.includes("User not found")) {
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
            return response.json();
        })
        .then(result => {
            if (result !== null) {
                addToast({
                    message: $_("ErrorMessages.usernameChangedSuccessfully"),
                    type: "success",
                });
                localStorage.setItem('username', result.newUsername);
                currentUsername = result.newUsername;
            }
        })
        .catch(error => {
            addToast({
                message: $_("ErrorMessages.somethingWentWrong"),
                type: "error",
            });
        })
        .finally(() => {
            username = '';
        });

    }

    onMount(() => {
        currentUsername = localStorage.getItem('username');
    });

</script>
<Container class="pt-5">
    <h1 class="text-center mb-5">{$_("ChangeUsername.changeUsername")}</h1>
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>{$_("ChangeUsername.currentUsername:")}<strong>{currentUsername}</strong></Label>
            <Input maxlength={USER_NAME_LENGTH} bind:value={username} required placeholder={$_("ChangeUsername.enterNewUsername")} />
        </FormGroup>
        <Container class="d-flex flex-column p-0">
            <Container class="text-center p-0 mb-3">
                <Button color="primary" on:click={handlechange}>
                    {$_("ChangeUsername.change")}
                </Button>
            </Container>
        </Container>        
    </Form>
</Container>