<script>
  import { json } from '@sveltejs/kit';
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
    import { onMount } from 'svelte';

    export let type;
    export let maxlength;

    let apiName;
    let fieldName;
    let newName = "";

    async function submitHandler() {
        newName = newName.trim();
        if (!newName) {
            return;
        }

        const payload = {
            [fieldName]: newName
        };

        fetch(`/api/create-${apiName}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    if (text.includes("Name already exists")) {
                        console.error("Error: Name already exists");
                    } else if(text.includes("Name cannot be empty")) {
                        console.error("Error: Name cannot be empty");
                    }
                    else {
                        console.error(`HTTP error! status: ${response.status}: ${text}`);
                    }
                    return null;
                });
            }
            return response.json();
        })
        .then(result => {
            if (result !== null) {
                console.log("works:", result);
            }
        })
        .catch(error => {
            console.error("error:", error);
        })
        .finally(() => {
            newName = "";
        });
    }


    onMount(() => {
        if(type === "Genre") {
            fieldName = "genreName";
            apiName = "genre";
        } else if(type === "Personnel") {
            fieldName = "personFullName";
            apiName = "person";
        }
    });

</script>

<Container>
    <h1>Create {type}</h1>
    <Form class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
        <Input 
            class="mb-4"
            maxlength={maxlength}
            type="text"
            bind:value={newName}
        />
        <Container class="d-flex justify-content-center">
            <Button color="primary" on:click={submitHandler}>Submit</Button>
        </Container>
    </Form>
</Container>

<style>
    h1 {
        padding:50px 0 50px 0;
        text-align: center;
    }
</style>
