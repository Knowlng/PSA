<script>
    import {
        Container,
        Button,
        FormGroup,
        Label,
        Input,
        Form
    } from '@sveltestrap/sveltestrap';

    let username = '';
    
    function handlechange() {
        console.log('Username: ', username);
    }

</script>
<!-- TBA: validation, depending on database -->
<Container class="pt-5">
    <h1 class="text-center mb-5">Change Username</h1>
    <Form class="w-75 mx-auto" style="min-width: 200px; max-width: 300px;"> 
        <FormGroup>
            <Label>Current username: ADD</Label>
            <Input placeholder="Enter new username" />
        </FormGroup>
        <Container class="d-flex flex-column p-0">
            <Container class="text-center p-0 mb-3">
                <Button color="primary" on:click={handlechange}>
                    Change
                </Button>
            </Container>
        </Container>        
    </Form>
</Container>