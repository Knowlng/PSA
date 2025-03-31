<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { addToast } from "$lib/ToastNotification/toastStore.js";
  import { Container } from '@sveltestrap/sveltestrap';
  import { goto } from '$app/navigation';
  import { filterStore } from '$lib/filterStore.js';


  let id = $page.params.id;
  let filmDetails;

  $: directors = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'director')
    : [];
  $: writers = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'writer')
    : [];
  $: actors = filmDetails?.actors
    ? filmDetails.actors.filter(actor => actor.role.toLowerCase() === 'actor')
    : [];

  async function getMovieInfo() {
    try {
      const response = await fetch(`/api/public/film/${id}`);
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
  
  onMount(() => {
    getMovieInfo();
  });
    
</script>
<Container class="d-flex justfy-content-center align-items-center">
    <Container class="d-flex justify-content-center align-items-center flex-column">
        <h1>{filmDetails?.filmName || ''}</h1>
    </Container>
    <Container class="pt-5">
        <p><strong>Release Date:</strong> {filmDetails?.filmReleaseDate || '-'}</p>
        <p><strong>Rating:</strong> {filmDetails?.filmRating || '-'}</p>
        <p><strong>Gross:</strong> ${filmDetails?.filmGross != null ? filmDetails.filmGross : '-'}</p>
    </Container>
</Container>
<Container>
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

  <h5><strong>Description</strong></h5>
  <p class='ps-3'>{filmDetails?.filmDesc || ''}</p>
</Container>

<style>
  button {
    all: unset;
    cursor: pointer;
  }
</style>