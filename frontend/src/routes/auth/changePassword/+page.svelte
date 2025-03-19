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
    import { PASS_REGEX, USER_PASS_LENGTH } from '$lib/consts.js';
    import { addToast } from "$lib/ToastNotification/toastStore.js";

    let password = '';
    let repeatedPass = '';

    let modalOpen = false;

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
        /*checks go here */
        modalOpen = true;
    }
    
    function confirmChange() {
        console.log('changing');
    }


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
        <Container class="text-center p-0 mb-3">
            <Button color="primary" on:click={changeHandler}>
                Change
            </Button>
        </Container>
    </Form>
</Container>
{#if modalOpen}
    <Modal 
        modalTitle={"Change password?"}
        modalBody={"This action can not be undone!"}
        buttonText="Change"
        on:toggle={() => { modalOpen = false; }}
        on:confirm={confirmChange}
    />
{/if}