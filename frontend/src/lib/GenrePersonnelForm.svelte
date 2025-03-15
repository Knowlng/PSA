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

    export let type;
    export let maxlength;

    let name = "";
    let selectedName = "";

    function submitHandler() {
        selectedName = selectedName.trim();
        if (!selectedName) {
            return;
        }
        console.log(selectedName);
    }

    function selectHandler(event) {
        selectedName = name.trim();
    }

</script>

<Container>
    <h1>Edit {type} Info</h1>
    <p>Update {type} info by either selecting an existing one from the list or defining a new entry</p>
    <Form class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
        <FormGroup class="mb-4">
        <SearchField 
            placeholder="Enter {type} name" 
            maxlength={maxlength} 
            bind:value={name} 
            on:enter={selectHandler} 
        />
        </FormGroup>
        {#if selectedName !== ""}
        <Input 
            class="mb-4"
            maxlength={maxlength}
            type="text"
            bind:value={selectedName}
            placeholder={selectedName}
        />
        <Container class="d-flex justify-content-center">
            <Button color="primary" on:click={submitHandler}>Submit</Button>
        </Container>
        {/if}
    </Form>
</Container>

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
    p {
        text-align: center;
    }
</style>
