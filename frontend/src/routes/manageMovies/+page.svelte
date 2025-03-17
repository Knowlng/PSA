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
  import {
    MAX_MOVIE_NAME_LENGTH,
    MAX_DESC_LENGTH,
    MAX_GENRE_SEARCH_LENGTH,
    MAX_PERSON_SEARCH_LENGTH,
    MAX_GROSS
  } from '$lib/consts.js';

  let movieName = "";
  let ageRating = "";
  let movieDate = "";
  let gross = "";
  let description = "";
  let actorName = '';
  let genreName = '';
  let movieId;

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

  async function submitHandler() {
    movieName = movieName.trim();
    description = description.trim();
    movieNameInvalid = !movieName;
    descriptionInvalid = !description;
    validateGross();

    if (!movieName || !description || grossInvalid) {
      return;
    }

    const payload = {
      filmName: movieName,
      filmDesc: description,
      filmReleaseDate: movieDate,
      filmGross: gross,
      filmRating: ageRating,
      genreIds: genreArray.map(genre => genre.id),
      persons: actorArray.map(actor => ({
        personId: actor.id,
        role: actor.role
      }))
    };

    const req = movieId ? `/update-film/${movieId}` : '/create-film';

    fetch(`/api${req}`, {
      method: movieId ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })
    .then(async response => {
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(result => {
      console.log("Works:", result);
    })
    .catch(error => {
      if (error.message === "Film with this name already exists") {
        console.error("Film with this name already exists");
      } else {
        console.error("Error:", error);
      }
    })
    .finally(() => {
      resetValues();
    });
  }

  function resetValues() {
    movieName = "";
    ageRating = "";
    movieDate = "";
    gross = "";
    description = "";
    actorArray = [];
    genreArray = [];
    actorName = "";
    genreName = "";
    movieNameInvalid = false;
    descriptionInvalid = false;
    grossInvalid = false;
    movieId = undefined;
  }

  function validateGross() {
    if (gross==="") {
      return;
    }
    const value = Number(gross);
    if (value <= 0 || !Number.isInteger(value) || value > MAX_GROSS) {
      grossInvalid = true;
    } else {
      grossInvalid = false;
    }
  }

  function handleActorEnter(event) {
    const { id, name } = event.detail;
    actorArray = [...actorArray, { id: id, name: name, role: "actor" }];
  }

  async function handleMovieEnter(event) {
    const { id } = event.detail;

    try {
      const response = await fetch(`/api/film/${id}`);
      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || `HTTP error! status: ${response.status}`);
      }

      const filmDetails = await response.json();
      movieId = filmDetails.id;
      movieName = filmDetails.filmName || "";
      ageRating = filmDetails.filmRating || "";
      movieDate = filmDetails.filmReleaseDate || "";
      gross = filmDetails.filmGross != null ? filmDetails.filmGross : "";
      description = filmDetails.filmDesc || "";
      actorArray = (filmDetails.actors || []).map(actor => ({
        id: actor.id,
        name: actor.name,
        role: actor.role
      }));
      genreArray = (filmDetails.genres || []).map(genre => ({
        id: genre.id,
        name: genre.name
      }));

    } catch (error) {
      console.error("Failed to fetch film details:", error);
    }
  }

  async function deleteMovie() {
    if (!movieId) {
      return;
    }

    try {
      const response = await fetch(`/api/delete-film/${movieId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Film not found")) {
          console.error("Film not found.");
        } else {
          console.error(`Deletion error: ${text}`);
        }
        return;
      }

      const result = await response.text(); 
      console.log("Deleted successfully:", result);

    } catch (error) {
      console.error("Failed to delete film:", error);
    }
    resetValues();
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
    const { id, name } = event.detail;
    genreArray = [...genreArray, { id, name }];
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
      <FormGroup class="mb-4">
          <SearchField
            placeholder="Enter movie name" 
            feedback="Can't be empty"
            maxlength={MAX_MOVIE_NAME_LENGTH} 
            bind:value={movieName} 
            invalid={movieNameInvalid} 
            on:enter={preventEnterSubmit}
            on:select={handleMovieEnter}
            on:fieldFocused={() => movieNameInvalid = false}
            searchEndpoint={`/api/search-film`}
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
            max={MAX_GROSS}
            feedback="Invalid gross amount"
            bind:value={gross} 
            invalid={grossInvalid} 
            on:focus={() => grossInvalid = false}
          />
        </InputGroup>
        <SearchField placeholder="Enter genre" maxlength={MAX_GENRE_SEARCH_LENGTH} bind:value={genreName} on:select={handleGenreEnter} searchEndpoint={`/api/search-genre`}/>
        <ListGroup flush class="mt-4">
          {#each genreArray as { id, name }, index}
          <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
            <Container class="p-0 w-25">
              {name}
            </Container>
            <Container class="d-flex justify-content-end p-0">
              <Button type="button" color="danger" on:click={() => removeGenre(index)}>Remove</Button>  
            </Container>
          </ListGroupItem>
        {/each}
        </ListGroup>
        <SearchField placeholder="Enter Actor Name" maxlength={MAX_PERSON_SEARCH_LENGTH} bind:value={actorName} on:select={handleActorEnter} searchEndpoint={`/api/search-person`}/>
        <ListGroup flush class="mt-4">
          {#each actorArray as { id, name}, index}
            <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
              <Container class="p-0 w-25">
                {name}
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
            maxlength={MAX_DESC_LENGTH} style="resize: none;" 
            required feedback="Can't be empty"
            invalid={descriptionInvalid}
            on:focus={() => descriptionInvalid = false}
          />
        </FormGroup>
        <Container class="text-center justify-content-between {movieId ? 'd-flex' : ''}">
          <Button type="submit" color="{movieId ? 'success' : 'primary'}" style="text-align:center;" on:click={submitHandler}>{movieId ? 'Change' : 'Create'}</Button>
          {#if movieId}
            <Button type="button" color="danger" style="text-align:center;" on:click={deleteMovie}>Delete</Button>
          {/if}
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
