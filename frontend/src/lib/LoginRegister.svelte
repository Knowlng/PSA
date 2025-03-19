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
            fetch(`/api/login`, {
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
                                message: "Invalid username or password",
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
                    localStorage.setItem('userLoggedIn', true);
                    window.location.href = '/';
                }
            })
            .catch(error => {
                addToast({
                    message: "Something went wrong. Please try again later.",
                    type: "error",
                });
            })
        } else {
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

            fetch(`/api/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        console.log(response.text());
                        if (text.includes("Username is already taken")) {
                            addToast({
                                message: "Username is already taken",
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
                    window.location.href = '/login';
                }
            })
            .catch(error => {
                addToast({
                    message: "Something went wrong. Please try again later.",
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
    <h1 class="text-center mb-5">{type === 'login' ? 'Log in' : 'Register'}</h1>
    {#if type === 'register'}
        <p class="text-center">Please provide a password that contains:</p>
        <ul class="text-center" style="list-style-type: none;">
            <li>At least <strong>8 characters</strong></li>
            <li>At least one <strong>uppercase letter</strong></li>
            <li>At least one <strong>number</strong></li>
            <li>At least one <strong>special character</strong></li>
        </ul>
    {/if}
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>Username</Label>
            <Input bind:value={username} maxlength={USER_NAME_LENGTH} required placeholder="Enter your username" />
        </FormGroup>
        <FormGroup>
            <Label>Password</Label>
            <Input bind:value={password} maxlength={USER_PASS_LENGTH} type={type === 'login' ? 'password' : ''} required placeholder="Enter your password" />
        </FormGroup>
        {#if type === 'register'}
            <FormGroup>
                <Label>Repeat password</Label>
                <Input bind:value={repeatedPass} maxlength={USER_PASS_LENGTH} required placeholder="Repeat your password" />
            </FormGroup>
        {/if}
        <Container class="d-flex flex-column p-0">
            <Container class="text-center p-0 mb-3">
                <Button color="primary" on:click={handleSubmit}>
                    {type === 'login' ? 'Login' : 'Register'}
                </Button>
            </Container>
            <Container class="d-flex p-0 justify-content-end">
                <a 
                    href={hrefLink} 
                    class="p-0" 
                    style="text-decoration: none; color: inherit;"
                >
                    {type === 'login' ? 'Register instead' : 'Login instead'}
                </a>
            </Container>
        </Container>        
    </Form>
</Container>