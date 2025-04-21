<script>
    import {
        Container,
        Button,
        FormGroup,
        Label,
        Input,
        Form
    } from '@sveltestrap/sveltestrap';
    import { PASS_REGEX, USER_NAME_LENGTH, USER_PASS_LENGTH } from '$lib/consts.js';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { onMount } from 'svelte';
    import { _ } from "svelte-i18n";

    export let type;

    let username = '';
    let password = '';
    let repeatedPass = '';

    let hrefLink = '';
    $: hrefLink = type === 'login' ? '/register' : '/login';

    function handleSubmit() {
        if (username.trim() === '' || password.trim() === '') {
            return;
        }
        username = username.toLowerCase();
        const payload = { userName: username, userPassword: password };

        if (type === 'login') {
            fetch(`/api/public/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        if (text.includes("Invalid username or password")) {
                            addToast({
                                message: $_("ErrorMessages.invalidUserNameOrPassword"),
                                type: "error",
                            });
                        } else if (text.includes("User account is disabled")) {
                            addToast({
                                message: $_("ErrorMessages.userAccountIsDisabled"),
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
                    localStorage.setItem('userLoggedIn', true);
                    localStorage.setItem('username', result.username);
                    localStorage.setItem('role', result.role);
                    window.location.href = '/';
                }
            })
            .catch(error => {
                addToast({
                    message: $_("ErrorMessages.somethingWentWrong"),
                    type: "error",
                });
            })
        } else {
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

            fetch(`/api/public/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        if (text.includes("Username is already taken")) {
                            addToast({
                                message: $_("ErrorMessages.usernameIsAlreadyTaken"),
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
                    window.location.href = '/login';
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
                password = '';
                repeatedPass = '';
            });
        }
    }
</script>

<Container class="pt-5">
    <h1 class="text-center mb-5">{type === 'login' ?  $_("LoginRegister.login") : $_("LoginRegister.register")}</h1>
    {#if type === 'register'}
        <p class="text-center">{$_("LoginRegister.pleaseProvideAPasswordThatContains:")}</p>
        <ul class="text-center" style="list-style-type: none;">
            <li>{$_("LoginRegister.atLeast")}<strong> 8 {$_("LoginRegister.characters")}</strong></li>
            <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.uppercaseLetter")}</strong></li>
            <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.number")}</strong></li>
            <li>{$_("LoginRegister.atLeastOne")}<strong>{$_("LoginRegister.specialCharacter")}</strong></li>
        </ul>
    {/if}
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>{$_("LoginRegister.username")}</Label>
            <Input bind:value={username} maxlength={USER_NAME_LENGTH} required placeholder={$_("LoginRegister.enterYourUserName")} />
        </FormGroup>
        <FormGroup>
            <Label>{$_("LoginRegister.password")}</Label>
            <Input bind:value={password} maxlength={USER_PASS_LENGTH} type={type === 'login' ? 'password' : ''} required placeholder={$_("LoginRegister.enterYourPassword")}  />
        </FormGroup>
        {#if type === 'register'}
            <FormGroup>
                <Label>{$_("LoginRegister.repeatPassword")}</Label>
                <Input bind:value={repeatedPass} maxlength={USER_PASS_LENGTH} required placeholder={$_("LoginRegister.repeatYourPassword")} />
            </FormGroup>
        {/if}
        <Container class="d-flex flex-column p-0">
            <Container class="text-center p-0 mb-4">
                <Button color="primary" on:click={handleSubmit}>
                    {type === 'login' ? $_("LoginRegister.login") : $_("LoginRegister.register")}
                </Button>
            </Container>
            <Container class="d-flex p-0 justify-content-end">
                <a 
                    href={hrefLink} 
                    class="p-0" 
                    style="text-decoration: underline; color: inherit; font-weight: bold;"
                >
                    {type === 'login' ? $_("LoginRegister.registerInstead")  : $_("LoginRegister.loginInstead")}
                </a>
            </Container>
        </Container>        
    </Form>
</Container>