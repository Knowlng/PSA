<script>
    import {
        Container,
        Button,
        FormGroup,
        Label,
        Input,
        Form
    } from '@sveltestrap/sveltestrap';
    import { PASS_REGEX } from '$lib/consts.js';

    export let type;

    let username = '';
    let password = '';
    let repeatedPass = '';

    let hrefLink = '';
    $: hrefLink = type === 'login' ? '/register' : '/login';


    function handlelogin() {
        if (type === 'login') {
            console.log('login');
        } else {
            if (!PASS_REGEX.test(password)) {
                console.log('Password does not meet the required criteria.');
                return;
            }
            if (password !== repeatedPass) {
                console.log('Passwords do not match.');
                return;
            }
            console.log('register');
        }
    }
    
</script>
<!-- TBA: validation, depending on database -->
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
            <Input bind:value={username} placeholder="Enter your username" />
        </FormGroup>
        <FormGroup>
            <Label>Password</Label>
            <Input bind:value={password} type={type === 'login' ? 'password' : ''} placeholder="Enter your password" />
        </FormGroup>
        {#if type === 'register'}
            <FormGroup>
                <Label>Repeat password</Label>
                <Input bind:value={repeatedPass} placeholder="Repeat your password" />
            </FormGroup>
        {/if}
        <Container class="d-flex flex-column p-0">
            <Container class="text-center p-0 mb-3">
                <Button color="primary" on:click={handlelogin}>
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