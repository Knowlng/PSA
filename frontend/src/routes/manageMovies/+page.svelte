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

  let movieName = "";
  let ageRating = "";
  let movieDate = "";
  let gross = "";
  let description = "";

  let inner = '';

  let movieNameInvalid = false;
  let descriptionInvalid = false;
  let grossInvalid = false;
  
  const resize = () => {
    inner.style.height = 'auto';
    inner.style.height = 4 + inner.scrollHeight + 'px';
  };

  function submitHandler() {
    movieName = movieName.trim();
    description = description.trim();
    movieNameInvalid = !movieName;
    descriptionInvalid = !description;
    validateGross();
    if (!movieName || !description || grossInvalid) {
      return;
    }
    console.log({
      movieName,
      ageRating,
      movieDate,
      gross,
      description
    });
  }

  function validateGross() {
    if (gross==="") {
      return;
    }
    const value = Number(gross);
    if (value <= 0 || !Number.isInteger(value) || value > 1000000000000) {
      grossInvalid = true;
    } else {
      grossInvalid = false;
    }
  }
</script>

<Container>
    <h1>Edit Movie Info</h1>
    <Form class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
      <FormGroup floating label="Move name" class="mb-4">
        <Input 
          bind:value={movieName} 
          maxlength="255" 
          required 
          feedback="Can't be empty" 
          invalid={movieNameInvalid}
          on:focus={() => movieNameInvalid = false}
        />
      </FormGroup>
      <InputGroup>
        <FormGroup floating label="Age rating" class="override-mb">
          <Input type="select" bind:value={ageRating}>
            <option value="">Select</option>
            <option value="G">G</option>
            <option value="PG">PG</option>
            <option value="PG-13">PG-13</option>
            <option value="R">R</option>
            <option value="NC-17">NC-17</option>
          </Input>
        </FormGroup>
        <Input type="date" bind:value={movieDate} />
        <InputGroupText>$</InputGroupText>
        <Input 
        placeholder="Gross" 
        type="number" step="1" min="0"
        max="1000000000000" 
        feedback="Invalid gross amount"
        bind:value={gross} 
        invalid={grossInvalid} 
        on:focus={() => grossInvalid = false}/>
      </InputGroup>
      <FormGroup class="mt-2">
        <Label>Description</Label>
        <Input 
          rows={1} type="textarea" bind:inner 
          on:input={resize} bind:value={description}
          maxlength="500" style="resize: none;" 
          required feedback="Can't be empty"
          invalid={descriptionInvalid}
          on:focus={() => descriptionInvalid = false}
        />
      </FormGroup>
      <Container class="text-center">
        <Button type="submit"  color="primary" style="text-align:center;" on:click={submitHandler} >Submit</Button>
      </Container>
  </Form>
</Container>

<style>
  h1 {
    padding:50px 0 50px 0;
    text-align: center;
  }

  :global(.input-group>.form-floating) {
    margin-bottom: 0 !important;
  }

  :global(input[type="number"]::-webkit-outer-spin-button),
  :global(input[type="number"]::-webkit-inner-spin-button) {
    -webkit-appearance: none;
    margin: 0;
  }

</style>
