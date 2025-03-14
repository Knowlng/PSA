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

  let movieName = "";
  let ageRating = "";
  let movieDate = "";
  let gross = "";
  let description = "";
  let actorName = '';
  let genreName = '';

  let genreArray = [];
  let actorArray = [];

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
      description,
      actors: actorArray,
      genre: genreArray
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

  function handleActorEnter(event) {
    const { value } = event.detail;
    if (value) {
      actorArray = [...actorArray, { name: value, role: "actor" }];
    }
  }


  function preventEnterSubmit(event) {
    if (event.key === "Enter") {
      event.preventDefault();
    }
  }

  function removeActor(index) {
    actorArray = actorArray.filter((_, i) => i !== index);
  }

  function handleGenreEnter(event) {
    const { value } = event.detail;
    if (value) {
      genreArray = [...genreArray, value];
    }
  }

  function removeGenre(index) {
    genreArray = genreArray.filter((_, i) => i !== index);
  }


</script>

<Container>
    <h1>Edit Movie Info</h1>
    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
    <div role="group" on:keydown={preventEnterSubmit}>
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
        <InputGroup class="mb-4">
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
            on:focus={() => grossInvalid = false}
          />
        </InputGroup>
        <SearchField placeholder="Enter genre" bind:value={genreName} on:enter={handleGenreEnter}/>
        <ListGroup flush class="mt-4">
          {#each genreArray as genre, index}
          <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
            <Container class="p-0 w-25">
              {genre}
            </Container>
            <Container class="d-flex justify-content-end p-0">
              <Button type="button" color="danger" on:click={() => removeGenre(index)}>Remove</Button>  
            </Container>
          </ListGroupItem>
        {/each}
        </ListGroup>
        <SearchField placeholder="Enter Actor Name" bind:value={actorName} on:enter={handleActorEnter}/>
        <ListGroup flush class="mt-4">
          {#each actorArray as actor, index}
            <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
              <Container class="p-0 w-25">
                {actor.name}
              </Container>
              <Container class="d-flex justify-content-end p-0">
                <Input type="select" class="w-25" bind:value={actorArray[index].role}>
                  {#each ["director", "writer", "actor"] as option}
                    <option value={option}>{option}</option>
                  {/each}
                </Input>
                <Button type="button" color="danger" on:click={() => removeActor(index)}>Remove</Button>  
              </Container>
            </ListGroupItem>
          {/each}
        </ListGroup>
        <FormGroup>
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
          <Button type="submit"  color="primary" style="text-align:center;" on:click={submitHandler}>Submit</Button>
        </Container>
    </Form>
  </div>
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

  :global(.list-group-item:last-child) {
    margin-bottom: 2rem;
  }

</style>
