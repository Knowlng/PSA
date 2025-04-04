<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { addToast } from "$lib/ToastNotification/toastStore.js";
  import { Container } from '@sveltestrap/sveltestrap';
  import { goto } from '$app/navigation';
  import { filterStore } from '$lib/filterStore.js';
  import Star from '$lib/Star.svelte';
  import Modal from '$lib/Modal.svelte';
  import StarRating from '$lib/StarRating.svelte';
  import { Spinner } from '@sveltestrap/sveltestrap';

  let loadingFilm = true;
  let loadingRating = true;

  let filmId = $page.params.id;
  let filmDetails;
  let modalOpen = false;

  let rating ='';
  let currentRating = 0;
  let averageRating = 0;

  $: directors = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'director')
    : [];
  $: writers = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'writer')
    : [];
  $: actors = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'actor')
    : [];

  async function fetchAverageRating() {
    try {
      const response = await fetch(`/api/public/film/${filmId}/average-rating`, {
        method: 'GET',
        credentials: 'include'
      });
      
      if (!response.ok) {
        addToast({
          message: "Something went wrong. Please try again later.",
          type: "error",
        });
        return;
      }
      
      const data = await response.json();
      averageRating = data.averageRating;
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error",
      });
    }
    loadingRating = false;
  }

  async function getMovieInfo() {
    try {
      const response = await fetch(`/api/public/film/${filmId}`);
      if (!response.ok) {
        const text = response.text();
        if(text.includes("Film not found")) {
          addToast({
            message: "Entry not found",
            type: "error",
          });
        } else {
          addToast({
            message: "Something went wrong. Please try again later.",
            type: "error",
          });
        }
      }

      filmDetails = await response.json();
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error",
      });
    }
    loadingFilm = false;
  }

  function handleNameClick(clickedPerson) {
    filterStore.set({
      movieName: '',
      ageRating: '',
      fromDate: '',
      toDate: '',
      minGross: '',
      maxGross: '',
      genreName: '',
      actorName: '',
      selectedAgeRatings: [],
      genreArray: [],
      actorArray: [{ actorId: clickedPerson.id, name: clickedPerson.name, role: clickedPerson.role }],
      perPage: "10",
      totalPages: 0,
      totalEntries: 0,
      currentPage: 1
    });
    goto("/");
  }
  
  async function confirmHandler() {
    if(rating === '') {
      return;
    }
    try {
      const response = await fetch(`/api/auth/rate-film`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ filmId, rating })
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Film not found")) {
          addToast({
            message: "Film not found",
            type: "error",
          });
        } else if (text.includes("User not found")) {
          addToast({
            message: "User not found",
            type: "error",
          });
        } else if (text.includes("Rating must be between")) {
          addToast({
            message: "Rating must be between 1 and 10",
            type: "error",
          });
        } else {
          addToast({
            message: "Something went wrong. Please try again later.",
            type: "error",
          });
        }
        return;
      }
      const result = await response.json();
      if (result) {
        addToast({
          message: "Rating saved successfully",
          type: "success",
        });
        currentRating = result.rating;
      }
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error",
      });
    }
    fetchAverageRating();
  }

  async function fetchUserFilmRating() {
    try {
      const response = await fetch(`/api/auth/film-rating?filmId=${filmId}`, {
        method: 'GET',
        credentials: 'include'
      });
      
      if (!response.ok) {
        const text = await response.text();
        if (text.includes("User not found")) {
          addToast({
            message: "User not found",
            type: "error"
          });
        } else {
          addToast({
            message: "Something went wrong. Please try again later.",
            type: "error"
          });
        }
        return;
      }
      
      const data = await response.json();
      currentRating = data.rating;
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error"
      });
    }
    loadingRating = false;
  }

  onMount(() => {
    getMovieInfo();
    fetchAverageRating();
    if(localStorage.getItem('userLoggedIn') === 'true') {
      fetchUserFilmRating();
    }
  });
</script>
{#if loadingFilm}
<Container class="d-flex justify-content-center align-items-center">
  <Spinner color="warning"/>
</Container>
{:else}
  <Container class="d-flex justfy-content-center align-items-center">
      <Container class="d-flex justify-content-center align-items-center flex-column">
        <h1>{filmDetails?.filmName || ''}</h1>
      </Container>
      <Container class="pt-5">
        <p><strong>Release Date:</strong> {filmDetails?.filmReleaseDate || '-'}</p>
        <p><strong>Age Rating:</strong> {filmDetails?.filmRating || '-'}</p>
        <p><strong>Gross:</strong> ${filmDetails?.filmGross != null ? filmDetails.filmGross : '-'}</p>
      </Container>
  </Container>
  <Container class="d-flex flex-row">
    <Container class="p-0">
      {#if actors.length > 0}
        <p>
          <strong>Actors:</strong>
          {#each actors as actor, i}
            <button 
              on:click={() => handleNameClick(actor)}>
                {actor.name}
            </button>{i < actors.length - 1 ? ', ' : ''}
          {/each}
        </p>
      {/if}

      {#if directors.length > 0}
        <p>
          <strong>Director/s:</strong>
          {#each directors as actor, i}
            <button
              on:click={() => handleNameClick(actor)}>
                {actor.name}
            </button>{i < directors.length - 1 ? ', ' : ''}
          {/each}
        </p>
      {/if}

      {#if writers.length > 0}
        <p>
          <strong>Writer/s:</strong>
          {#each writers as actor, i}
            <button
              on:click={() => handleNameClick(actor)}>
                {actor.name}
            </button>{i < writers.length - 1 ? ', ' : ''}
          {/each}
        </p>
      {/if}
    </Container>
    {#if loadingRating}
      <Container class="d-flex justify-content-center align-items-center">
        <Spinner color="warning"/>
      </Container>
    {:else}
      <Container class="d-flex flex-column align-items-center justify-content-center">
        <Container class="d-flex justify-content-center align-items-center">
          <p class="mb-0 me-2"><strong>Overall rating:</strong></p>
          <strong><span class="me-1" style="font-size: 1.5rem;">{averageRating}</span></strong>
        <Star filled={true} color="yellow" size={40}/>
        </Container>
        {#if localStorage.getItem('userLoggedIn') === 'true'}
          <Container class="d-flex justify-content-center align-items-center">
            <p class="mb-0 me-2"><strong>Your rating:</strong></p>
            <strong><span class="me-1" style="font-size: 1.5rem;">{currentRating}</span></strong>
            <Star filled={currentRating ? true : false} color="blue" size={40} clickable={true} on:click={()=> modalOpen = true}/>
              {#if currentRating > 0}
                <p class="ms-2 mb-0"><em>(Click to update)</em></p>
              {:else}
                <p class="ms-2 mb-0"><em>(Click to rate)</em></p>
              {/if}
          </Container>
        {/if}
      </Container>
    {/if}
  </Container>
  <Container>
    <h5><strong>Description</strong></h5>
    <p class='ps-3 long-text'>{filmDetails?.filmDesc || ''}</p>
  </Container>
{/if}
{#if modalOpen}
    <Modal 
      modalTitle={"Rating " + filmDetails?.filmName || ''}
      modalBody={ currentRating ? "Update current rating of " + currentRating : "Rate this movie:"}
      buttonText={"Rate " + rating}
      buttonColor="primary"
      on:toggle={() => { modalOpen = false; rating = ''; }}
      on:confirm={confirmHandler}
    >
    <Container class="d-flex align-items-center justify-content-center">
      <StarRating
        rating={currentRating}
        on:rate={(e) => {
          rating = e.detail.rating;
        }}
      />
    </Container>
    </Modal>
{/if}

<style>
  button {
    all: unset;
    cursor: pointer;
    text-decoration: underline;
  }

  button:hover {
    color: var(--information);
  }

  .long-text {
    white-space: normal;
    overflow-wrap: break-word;
    word-wrap: break-word;
  }
</style>