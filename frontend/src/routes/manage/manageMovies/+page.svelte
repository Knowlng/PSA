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
  import { addToast } from "$lib/ToastNotification/toastStore.js";
  import Modal from '$lib/Modal.svelte';
  import { _, locale } from 'svelte-i18n';

  let modalOpen = false;

  let movieNameEn = "";
  let movieNameLt = "";
  let ageRating = "";
  let movieDate = "";
  let gross = "";
  let descriptionEn = "";
  let descriptionLt = "";
  let actorName = '';
  let genreName = '';
  let movieId;

  let genreArray = [];
  let actorArray = [];

  let inner='';

  let response;

  let movieNameInvalidEn = false;
  let movieNameInvalidLt = false;
  let descriptionInvalidEn = false;
  let descriptionInvalidLt = false;
  let grossInvalid = false;

  const resize = () => {
    inner.style.height = 'auto';
    inner.style.height = 4 + inner.scrollHeight + 'px';
  };

  async function submitHandler() {
    movieNameEn = movieNameEn.trim();
    descriptionEn = descriptionEn.trim();
    movieNameLt = movieNameLt.trim();
    descriptionLt = descriptionLt.trim();

    movieNameInvalidEn = !movieNameEn;
    movieNameInvalidLt = !movieNameLt;
    descriptionInvalidEn = !descriptionEn;
    descriptionInvalidLt = !descriptionLt;
    validateGross();

    if (!movieNameEn || !descriptionEn || grossInvalid || !movieNameLt || !descriptionLt) {
      return;
    }

    const payload = {
      filmNameEn: movieNameEn,
      filmNameLt: movieNameLt,
      filmDescEn: descriptionEn,
      filmDescLt: descriptionLt,
      filmReleaseDate: movieDate,
      filmGross: gross,
      filmRating: ageRating,
      genreIds: genreArray.map(genre => genre.id),
      persons: actorArray.map(actor => ({
        personId: actor.id,
        role: actor.role
      }))
    };

    const req = movieId ? `/admin/update-film/${movieId}` : '/admin/create-film';

    fetch(`/api${req}`, {
      method: movieId ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload),
      credentials: 'include'
    })
    .then(async res => {
      response = res;
      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Name already exists")) {
          addToast({
            message: $_("ErrorMessages.nameAlreadyExists"),
            type: "error",
          });
        } else if (text.includes("Film not found")) {
          addToast({
            message: $_("ErrorMessages.entryNotFound"),
            type: "error",
          });
        } else if (text.includes("English name already exists")) {
          addToast({
            message: $_("ErrorMessages.englishEntryAlreadyExists"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error",
          });
        }
      } else {
        if (movieId) {
          addToast({
            message: $_("ErrorMessages.updatedSuccessfully"),
            type: "success",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.createdSuccessfully"),
            type: "success",
          });
        }
      }
    })
    .catch(error => {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
    })
    .finally(() => {
      if (response && response.ok) {
        resetValues();
      }
    });
  }

  function resetValues() {
    movieNameEn = "";
    movieNameLt = "";
    ageRating = "";
    movieDate = "";
    gross = "";
    descriptionEn = "";
    descriptionLt = "";
    actorArray = [];
    genreArray = [];
    actorName = "";
    genreName = "";
    movieNameInvalidLt = false;
    movieNameInvalidEn = false;
    descriptionInvalidEn = false;
    descriptionInvalidLt = false;
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

  function handlePersonEnter(event) {
    const { id, name } = event.detail;
    actorArray = [...actorArray, { id: id, name: name, role: "actor" }];
  }

  async function handleMovieEnter(event) {
    const { id } = event.detail;

    try {
      const response = await fetch(`/api/public/film/${id}?locale=${$locale}`);
      if (!response.ok) {
        const text = response.text();
        if(text.includes("Film not found")) {
          addToast({
            message: $_("ErrorMessages.entryNotFound"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error",
          });
        }
      }

      const filmDetails = await response.json();
      movieId = filmDetails.id;
      movieNameEn = filmDetails.filmNameEn || "";
      movieNameLt = filmDetails.filmNameLt || "";
      ageRating = filmDetails.filmRating || "";
      movieDate = filmDetails.filmReleaseDate || "";
      gross = filmDetails.filmGross != null ? filmDetails.filmGross : "";
      descriptionEn = filmDetails.filmDescEn || "";
      descriptionLt = filmDetails.filmDescLt || "";
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
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
    }
  }

  async function deleteMovie() {
    if (!movieId) {
      return;
    }
    try {
      const response = await fetch(`/api/admin/delete-film/${movieId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include'
      });
      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Film not found")) {
          addToast({
            message: $_("ErrorMessages.entryNotFound"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error",
          });
        }
        return;
      } else {
        addToast({
          message: $_("ErrorMessages.deletedSuccessfully"),
          type: "success",
        });
      }
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
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
  <h1>{$_("manageMovies.editMovieInfo")}</h1>
    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
  <div role="group" on:keydown={preventEnterSubmit}>
    <Form class="w-75 mx-auto mb-3" style="min-width: 100px; max-width: 700px;">
      <FormGroup class="mb-4">
          <SearchField
            placeholder={$_("manageMovies.enterEnglishMovieName")} 
            feedback={$_("manageMovies.cantBeEmpty")}
            maxlength={MAX_MOVIE_NAME_LENGTH}
            bind:value={movieNameEn}
            invalid={movieNameInvalidEn}
            on:enter={preventEnterSubmit}
            on:select={handleMovieEnter}
            on:fieldFocused={() => movieNameInvalidEn = false}
            searchEndpoint={`/api/public/search-film`}
            language={'en'}
          />
        </FormGroup>
        <FormGroup class="mb-4">
          <SearchField
            placeholder={$_("manageMovies.enterLithuanianMovieName")}
            feedback={$_("manageMovies.cantBeEmpty")}
            maxlength={MAX_MOVIE_NAME_LENGTH} 
            bind:value={movieNameLt}
            invalid={movieNameInvalidLt} 
            on:enter={preventEnterSubmit}
            on:select={handleMovieEnter}
            on:fieldFocused={() => movieNameInvalidLt = false}
            searchEndpoint={`/api/public/search-film`}
            language={'lt'}
          />
        </FormGroup>
        <InputGroup class="mb-4 age-date-gross">
          <FormGroup floating label={$_("manageMovies.ageRating")} class="override-mb">
            <Input type="select" bind:value={ageRating}>
              <option value="">{$_("manageMovies.select")}</option>
              <option value="G">G</option>
              <option value="PG">PG</option>
              <option value="PG-13">PG-13</option>
              <option value="R">R</option>
              <option value="NC-17">NC-17</option>
            </Input>
          </FormGroup>
          <Input type="date" bind:value={movieDate} />
          <InputGroupText class='dollar-sign'>$</InputGroupText>
          <Input 
            placeholder={$_("manageMovies.gross")}
            type="number" step="1" min="0"
            max={MAX_GROSS}
            feedback={$_("manageMovies.invalidGrossAmount")}
            bind:value={gross} 
            invalid={grossInvalid} 
            on:focus={() => grossInvalid = false}
          />
        </InputGroup>
        <SearchField placeholder={$_("manageMovies.enterGenre")} maxlength={MAX_GENRE_SEARCH_LENGTH} bind:value={genreName} on:select={handleGenreEnter} searchEndpoint={`/api/public/search-genre`} clearOnSelect={true}/>
        <ListGroup flush class="mt-4">
          {#each genreArray as { id, name }, index}
          <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
            <Container class="p-0 w-25">
              {name}
            </Container>
            <Container class="d-flex justify-content-end p-0">
              <Button type="button" color="danger" on:click={() => removeGenre(index)}>{$_("manageMovies.remove")}</Button>  
            </Container>
          </ListGroupItem>
        {/each}
        </ListGroup>
        <SearchField placeholder={$_("manageMovies.enterPersonName")} maxlength={MAX_PERSON_SEARCH_LENGTH} bind:value={actorName} on:select={handlePersonEnter} searchEndpoint={`/api/public/search-person`} clearOnSelect={true}/>
        <ListGroup flush class="mt-4">
          {#each actorArray as { id, name}, index}
            <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
              <Container class="p-0 w-25">
                {name}
              </Container>
              <Container class="d-flex justify-content-end p-0">
                <Input type="select" class="w-50" bind:value={actorArray[index].role}>
                  <option value="director">{$_("manageMovies.director")}</option>
                  <option value="writer">{$_("manageMovies.writer")}</option>
                  <option value="actor">{$_("manageMovies.actor")}</option>
                </Input>
                <Button type="button" color="danger" on:click={() => removeActor(index)}>{$_("manageMovies.remove")}</Button>  
              </Container>
            </ListGroupItem>
          {/each}
        </ListGroup>
        <FormGroup>
          <Label>{$_("manageMovies.englishDescription")}</Label>
          <Input 
            rows={1} type="textarea" bind:inner
            on:input={resize} bind:value={descriptionEn}
            maxlength={MAX_DESC_LENGTH} style="resize: none;" 
            required feedback={$_("manageMovies.cantBeEmpty")}
            invalid={descriptionInvalidEn}
            on:focus={() => descriptionInvalidEn = false}
          />
        </FormGroup>
        <FormGroup>
          <Label>{$_("manageMovies.englishDescription")}</Label>
          <Input 
            rows={1} type="textarea" bind:inner 
            on:input={resize} bind:value={descriptionLt}
            maxlength={MAX_DESC_LENGTH} style="resize: none;" 
            required feedback={$_("manageMovies.cantBeEmpty")}
            invalid={descriptionInvalidLt}
            on:focus={() => descriptionInvalidLt = false}
          />
        </FormGroup>
        <Container class="text-center justify-content-between {movieId ? 'd-flex' : ''}">
          <Button type="submit" color="{movieId ? 'success' : 'primary'}" style="text-align:center;" on:click={submitHandler}>{movieId ? $_("manageMovies.change") : $_("manageMovies.create")}</Button>
            {#if movieId}
              <Button type="button" color="danger" style="text-align:center;" on:click={() => { modalOpen = true; }}>{$_("manageMovies.delete")}</Button>
            {/if}
        </Container>
    </Form>
  </div>
</Container>
{#if modalOpen}
    <Modal 
      modalTitle={$_("manageMovies.areYouSureYouWantToDelete") + movieNameEn + "?"}
      modalBody={$_("manageMovies.thisWillRemove") + movieNameEn + $_("manageMovies.fromThePublicListing")}
      buttonText={$_("manageMovies.delete")}
      on:toggle={() => { modalOpen = false; }}
      on:confirm={deleteMovie}
    />
{/if}

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

  @media (max-width: 500px) {
    :global(.age-date-gross) {
      display:flex;
      flex-direction: column;
      gap: 2rem;
    }

    :global(.input-group>.form-floating, .input-group>.form-control) {
      width:100%;
    }
    :global(.dollar-sign) {
      display:none;
    }
  }

</style>
