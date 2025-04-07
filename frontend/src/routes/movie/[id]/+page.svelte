<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { addToast } from "$lib/ToastNotification/toastStore.js";
  import { goto } from '$app/navigation';
  import { filterStore } from '$lib/filterStore.js';
  import Star from '$lib/Star.svelte';
  import Modal from '$lib/Modal.svelte';
  import StarRating from '$lib/StarRating.svelte';
  import { Container, Spinner, Form, Input, Button, Label } from '@sveltestrap/sveltestrap';
  import { COMMENT_MAX_LENGTH } from '$lib/consts.js';
  import CustomPagination from '$lib/CustomPagination.svelte';
  import CommentCard from '$lib/CommentCard.svelte';

  let totalPages;
  let totalEntries;
  let perPage;
  let currentPage;

  let loadingFilm = true;
  let loadingRating = true;
  let loadingComments = false;

  let filmId = $page.params.id;
  let filmDetails;
  let modalOpen = false;

  let rating ='';
  let currentRating = 0;
  let averageRating = 0;

  let inner ='';

  let commentText = '';
  let showNotSavedText = false;

  let comments = [];

  const resize = () => {
    inner.style.height = 'auto';
    inner.style.height = 4 + inner.scrollHeight + 'px';
  };

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

  function handlePageChange(event) {
    const { page } = event.detail;
    currentPage = page;
    fetchComments(page, Number(perPage));
  }

  function handlePerPageChange(event) {
    perPage = event.target.value;
    fetchComments(currentPage, Number(perPage));
  }

  async function fetchComments(page, perPage) {
    loadingComments = true;
    try {
      const response = await fetch('/api/public/film/comments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          filmId: filmId,
          page: page,
          size: perPage
        })
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Film not found")) {
          addToast({
            message: "Film not found",
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

      const data = await response.json();
      comments = data.content;
      totalPages = data.totalPages;
      totalEntries = data.totalElements;
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error",
      });
    }
    loadingComments = false;
  }


  async function fetchUserComment() {
    try {
      const response = await fetch(`/api/auth/comment?filmId=${filmId}`, {
        method: 'GET',
        credentials: 'include'
      });
      
      if (!response.ok) {
        addToast({
          message: "Something went wrong. Please try again later.",
          type: "error"
        });
        return null;
      }
      
      const data = await response.json();
      const storedReview = localStorage.getItem('yourReview' + filmId) || '';
      const dataReview = data.commentText || '';
      if(storedReview && storedReview !== dataReview) {
        commentText = storedReview;
        showNotSavedText = true;
      } else {
        commentText = dataReview;
        showNotSavedText = false;
      }
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error"
      });
      return null;
    }
  }

  async function handleCommentDelete() {
    try {
      const response = await fetch(`/api/auth/comment?filmId=${filmId}`, {
        method: 'DELETE',
        credentials: 'include'
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Comment not found")) {
          addToast({
            message: "No comment found to delete.",
            type: "error"
          });
        } else if (text.includes("User not found")) {
          addToast({
            message: "User not found.",
            type: "error"
          });
        } else if (text.includes("Unauthorized")) {
          addToast({
            message: "You must be logged in to delete a comment.",
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

      addToast({
        message: "Comment deleted successfully",
        type: "success"
      });

      commentText = '';
      showNotSavedText = false;
      localStorage.removeItem('yourReview' + filmId);

    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error"
      });
    }
  }


  async function handleCommentSubmit() {
    commentText = commentText.trim();
    const payload = {
      filmId,
      commentText
    };

    try {
      const response = await fetch('/api/auth/comment', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        addToast({
          message: "Something went wrong. Please try again later.",
          type: "error"
        });
        return;
      } else {
        addToast({
          message: "Comment saved successfully",
          type: "success"
        });
        showNotSavedText = false;
      }
    } catch (error) {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error"
      });
    }
  }
  function saveComment() {
    setTimeout(() => {
      localStorage.setItem('yourReview' + filmId, commentText);
      showNotSavedText = true;
    }, 0);
  }

  async function handleLikeToggle() {

  }

  onMount(() => {
    getMovieInfo();
    fetchAverageRating();
    if(localStorage.getItem('userLoggedIn') === 'true') {
      fetchUserFilmRating();
      fetchUserComment();
    }
    fetchComments(1, 10);
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
      {#if filmDetails.genres && filmDetails.genres.length > 0}
        <p>
          <strong>Genres:</strong>
          <span>
            {#each filmDetails.genres as genre, i}
              {i > 0 ? ', ' : ''}{genre.name ? genre.name : genre}
            {/each}
          </span>
        </p>
      {/if}

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
  <Container class='mb-4'>
    <h5><strong>Description</strong></h5>
    <p class='ps-3 long-text'>{filmDetails?.filmDesc || ''}</p>
  </Container>
  <Container class='d-flex justify-content-center align-items-center mb-4 flex-column'>
    <h5 class='mb-4'><strong>Your review</strong></h5>
    {#if localStorage.getItem('userLoggedIn') === 'true'}
      <Container class="d-flex justify-content-center align-items-center">
        <Container style="width: 100%; max-width: 700px;">
          <Form>
            <Input 
              rows={5} type="textarea" bind:inner 
              on:input={resize} bind:value={commentText}
              style="resize: none;"
              maxlength={COMMENT_MAX_LENGTH}
              placeholder="Write your review here..."
              on:keydown={saveComment}
              on:keypress={saveComment}
            />
            <p class='m-0'><em>{showNotSavedText ? 'Review not saved yet' : ''}</em></p>
            <Container class='d-flex mt-4 justify-content-between align-items-center'>
              <Button type="submit" color="danger" on:click={handleCommentDelete}>Delete</Button>
              <Button type="submit" color="primary" on:click={handleCommentSubmit}>Save</Button>
            </Container>
          </Form>
        </Container>
      </Container>
    {/if}
    <h5 class='mb-4 mt-4'><strong>All reviews</strong></h5>
    <Container>
      {#if totalPages > 1}
        <Container class="d-flex justify-content-center mb-1">
          <CustomPagination on:pageChange={handlePageChange} pageCount={totalPages} currentPage={currentPage}/>
        </Container>
      {/if}
    </Container>
    {#if loadingComments}
    <Container class="d-flex justify-content-center align-items-center">
      <Spinner color="warning"/>
    </Container>
    {:else if comments && comments.length > 0}
        <Container class="d-flex justify-content-end align-items-center mb-3" style="min-width: 450px; max-width: 700px;">
          <Container>Total pages: {totalPages}</Container>
          <Container class="text-center">Total Entries: {totalEntries}</Container>
          <Container style="min-width: 100px; max-width: 100px;" class="text-end p-0 d-flex align-items-center justify-content-end">
            <Label class="m-0" for="perPage">Per page:</Label>
          </Container>
          <Container style="min-width: 100px; max-width: 100px;">
            <select key={perPage} type="select" bind:value={perPage} on:change={handlePerPageChange}>
              <option value="10">10</option>
              <option value="25">25</option>
              <option value="50">50</option>
              <option value="75">75</option>
              <option value="100">100</option>
            </select>
          </Container>
        </Container>
    {:else}
      <Container class="text-center mt-4">
        <h4>No Reviews found</h4>
        <h4>Be the first one to leave a review!</h4>
      </Container>
    {/if}
    </Container>
    <Container>
      {#each comments as comment, index (`${filmId}-${comment.userId}`)}
        <CommentCard username={comment.userName} commentText={comment.commentText} score={comment.userRating} on:likeToggle={handleLikeToggle}/>
      {/each}
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