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
  import { COMMENT_MAX_LENGTH, DEFAULT_PAGE_SIZE } from '$lib/consts.js';
  import CustomPagination from '$lib/CustomPagination.svelte';
  import CommentCard from '$lib/CommentCard.svelte';
  import { _, locale } from "svelte-i18n";

  const COMMENT_FILTER_VALUES = [
    { id: 1, label: $_("MovieIdPage.byStars") },
    { id: 2, label: $_("MovieIdPage.likedByYou") },
    { id: 3, label: $_("MovieIdPage.dislikedByYou") }
  ];

  let totalPages;
  let totalEntries;
  let perPage = DEFAULT_PAGE_SIZE.toString();
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

  let filteredOptions;

  let filterTypeId;
  let sortOrder;

  let isCommentDeletable;
  let isUsernameRoutable;

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
          message: $_("ErrorMessages.somethingWentWrong"),
          type: "error",
        });
        return;
      }
      
      const data = await response.json();
      averageRating = data.averageRating;
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
    }
    loadingRating = false;
  }

  async function getMovieInfo() {
    try {
      const response = await fetch(`/api/public/film/${filmId}?locale=${$locale}`);
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

      filmDetails = await response.json();
      if($locale = 'lt') {
        filmDetails.filmName = filmDetails.filmNameLt;
        filmDetails.filmDesc = filmDetails.filmDescLt;
      } else if($locale = 'en') {
        filmDetails.filmName = filmDetails.filmNameEn;
        filmDetails.filmDesc = filmDetails.filmDescEn;
      }
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
    }
    loadingFilm = false;
  }

  async function handleAdminCommentDelete(event) {
    const { userId } = event.detail;

    try {
      const response = await fetch(`/api/admin/comment?userId=${userId}&filmId=${filmId}`, {
        method: 'DELETE',
        credentials: 'include',
      });
      
      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Comment not found")) {
          addToast({
            message: $_("ErrorMessages.commentNotFound"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error",
          });
        }
        return;
      }
    
      addToast({
        message: $_("ErrorMessages.commentDeletedSuccessfully"),
        type: "success",
      });
    
      fetchComments();
    
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
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
      perPage: DEFAULT_PAGE_SIZE.toString(),
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
            message: $_("ErrorMessages.filmNotFound"),
            type: "error",
          });
        } else if (text.includes("User not found")) {
          addToast({
            message: $_("ErrorMessages.userNotFound"),
            type: "error",
          });
        } else if (text.includes("Rating must be between")) {
          addToast({
            message: $_("ErrorMessages.raitingMustBeBetween"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error",
          });
        }
        return;
      }
      const result = await response.json();
      if (result) {
        addToast({
          message: $_("ErrorMessages.raitingSavedSuccessfully"),
          type: "success",
        });
        currentRating = result.rating;
      }
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
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
            message: $_("ErrorMessages.userNotFound"),
            type: "error"
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error"
          });
        }
        return;
      }
      
      const data = await response.json();
      currentRating = data.rating;
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
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
    fetchComments(1, Number(perPage));
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
          size: perPage,
          filterTypeId: filterTypeId,
          sortOrder: sortOrder
        })
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("Film not found")) {
          addToast({
            message: $_("ErrorMessages.filmNotFound"),
            type: "error",
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
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
        message: $_("ErrorMessages.somethingWentWrong"),
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
          message: $_("ErrorMessages.somethingWentWrong"),
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
        message: $_("ErrorMessages.somethingWentWrong"),
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
            message: $_("ErrorMessages.noCommentFoundToDelete"),
            type: "error"
          });
        } else if (text.includes("User not found")) {
          addToast({
            message: $_("ErrorMessages.userNotFound"),
            type: "error"
          });
        } else if (text.includes("Unauthorized")) {
          addToast({
            message: $_("ErrorMessages.commentDeleteNotAuthorized"),
            type: "error"
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error"
          });
        }
        return;
      }

      addToast({
        message: $_("ErrorMessages.commentDeletedSuccessfully"),
        type: "success"
      });

      commentText = '';
      showNotSavedText = false;
      localStorage.removeItem('yourReview' + filmId);

    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
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
          message:  $_("ErrorMessages.somethingWentWrong"),
          type: "error"
        });
        return;
      } else {
        addToast({
          message:  $_("ErrorMessages.commentSavedSuccessfully"),
          type: "success"
        });
        showNotSavedText = false;
      }
    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
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

  function filterComments(){
    fetchComments(1, Number(perPage));
  }

  async function handleCommentRate(event) {
    const { rating, userId } = event.detail;
    let commentUserId = userId;
    let commentRating = rating;

    const payload = {
      filmId,
      commentUserId,
      commentRating
    };

    try {
      const response = await fetch('/api/auth/rate-comment', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const text = await response.text();
        if (text.includes("User not found")) {
          addToast({
            message: $_("ErrorMessages.userNotFound"),
            type: "error"
          });
        } else if (text.includes("Film not found")) {
          addToast({
            message: $_("ErrorMessages.filmNotFound"),
            type: "error"
          });
        } else if (text.includes("Comment not found")) {
          addToast({
            message: $_("ErrorMessages.commentNotFound"),
            type: "error"
          });
        } else {
          addToast({
            message: $_("ErrorMessages.somethingWentWrong"),
            type: "error"
          });
        }
        return;
      }

    } catch (error) {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error"
      });
    }
  }

  onMount(() => {
    let userLoggedIn = localStorage.getItem('userLoggedIn') === 'true';
    filteredOptions = userLoggedIn
      ? COMMENT_FILTER_VALUES
      : COMMENT_FILTER_VALUES.slice(0, 1);
    isCommentDeletable = localStorage.getItem('role') === 'admin' ? true : false;
    isUsernameRoutable = localStorage.getItem('role') === 'admin' ? true : false;

    getMovieInfo();
    fetchAverageRating();
    if(localStorage.getItem('userLoggedIn') === 'true') {
      fetchUserFilmRating();  
      fetchUserComment();
    }
    fetchComments(1, DEFAULT_PAGE_SIZE);
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
        <p><strong>{$_("MovieIdPage.releaseDate:")}</strong> {filmDetails?.filmReleaseDate || '-'}</p>
        <p><strong>{$_("MovieIdPage.ageRating:")}</strong> {filmDetails?.filmRating || '-'}</p>
        <p><strong>{$_("MovieIdPage.gross:")}</strong> ${filmDetails?.filmGross != null ? filmDetails.filmGross : '-'}</p>
      </Container>
  </Container>
  <Container class="d-flex flex-row second-info-containter">
    <Container class="p-0">
      {#if filmDetails.genres && filmDetails.genres.length > 0}
        <p>
          <strong>{$_("MovieIdPage.genres:")}</strong>
          <span>
            {#each filmDetails.genres as genre, i}
              {i > 0 ? ', ' : ''}{genre.name ? genre.name : genre}
            {/each}
          </span>
        </p>
      {/if}

      {#if actors.length > 0}
        <p>
          <strong>{$_("MovieIdPage.actors:")}</strong>
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
          <strong>{$_("MovieIdPage.directors:")}</strong>
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
          <strong>{$_("MovieIdPage.writers:")}</strong>
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
          <p class="mb-0 me-2"><strong>{$_("MovieIdPage.overallRating:")}</strong></p>
          <strong><span class="me-1" style="font-size: 1.5rem;">{averageRating}</span></strong>
        <Star filled={true} color="yellow" size={40}/>
        </Container>
        {#if localStorage.getItem('userLoggedIn') === 'true'}
          <Container class="d-flex justify-content-center align-items-center">
            <p class="mb-0 me-2 text-center"><strong>{$_("MovieIdPage.yourRating:")}</strong></p>
            <strong><span class="me-1" style="font-size: 1.5rem;">{currentRating}</span></strong>
            <Star filled={currentRating ? true : false} color="blue" size={40} clickable={true} on:click={()=> modalOpen = true}/>
              {#if currentRating > 0}
                <p class="ms-2 mb-0"><em>{$_("MovieIdPage.clickToUpdate")}</em></p>
              {:else}
                <p class="ms-2 mb-0"><em>{$_("MovieIdPage.clickToRate")}</em></p>
              {/if}
          </Container>
        {/if}
      </Container>
    {/if}
  </Container>
  <Container class='mb-4'>
    <h5><strong>{$_("MovieIdPage.description")}</strong></h5>
    <p class='ps-3 long-text'>{filmDetails?.filmDesc || ''}</p>
  </Container>
  <Container class='d-flex justify-content-center align-items-center mb-4 flex-column'>
    {#if localStorage.getItem('userLoggedIn') === 'true'}
      <h5 class='mb-4'><strong>{$_("MovieIdPage.yourReview")}</strong></h5>
      <Container class="d-flex justify-content-center align-items-center">
        <Container style="width: 100%; max-width: 700px;">
          <Form>
            <Input 
              rows={5} type="textarea" bind:inner 
              on:input={resize} bind:value={commentText}
              style="resize: none;"
              maxlength={COMMENT_MAX_LENGTH}
              placeholder={$_("MovieIdPage.writeYourReviewHere")}
              on:keydown={saveComment}
              on:keypress={saveComment}
            />
            <p class='m-0'><em>{showNotSavedText ? $_("MovieIdPage.reviewNotSavedYet") : ''}</em></p>
            <Container class='d-flex mt-4 justify-content-between align-items-center'>
              <Button type="submit" color="danger" on:click={handleCommentDelete}>{$_("MovieIdPage.delete")}</Button>
              <Button type="submit" color="primary" on:click={handleCommentSubmit}>{$_("MovieIdPage.save")}</Button>
            </Container>
          </Form>
        </Container>
      </Container>
    {/if}
    <h5 class='mb-4 mt-4'><strong>{$_("MovieIdPage.allReviews")}</strong></h5> 
    <Container class="d-flex justify-content-center align-items-center mb-3 gap-3 filter-container">
      <Input bind:value={filterTypeId} type="select" style="width:200px;" class="filter-select">
        {#each filteredOptions as option}
          <option value={option.id}>{option.label}</option>
        {/each}
      </Input>
      <Input  bind:value={sortOrder} type="select" style="width:150px;" class="filter-select-order">
        <option default value="DESC">{$_("MovieIdPage.sortOrderDESC")}</option>
        <option value="ASC">{$_("MovieIdPage.sortOrderASC")}</option>
      </Input>
      <Button color="primary" type="submit" on:click={filterComments}>{$_("MovieIdPage.filter")}</Button>
    </Container>
      {#if totalPages > 1}
        <Container class="d-flex justify-content-center mb-1">
          <CustomPagination on:pageChange={handlePageChange} pageCount={totalPages} currentPage={currentPage}/>
        </Container>
      {/if}
    {#if loadingComments}
    <Container class="d-flex justify-content-center align-items-center">
      <Spinner color="warning"/>
    </Container>
    {:else if comments && comments.length > 0}
        <Container class="d-flex justify-content-end align-items-center mb-3" style="min-width: 100px; max-width: 700px;">
          <Container class="text-center">{$_("MovieIdPage.totalPages:")} {totalPages}</Container>
          <Container class="text-center">{$_("MovieIdPage.totalEntries:")} {totalEntries}</Container>
          <Container class="d-flex per-page-panel text-center">
            <Container style="min-width: 100px; max-width: 100px;" class="per-page-label text-end p-0 d-flex align-items-center justify-content-end">
              <Label class="m-0" for="perPage">{$_("MovieIdPage.perPage:")}</Label>
            </Container>
            <Container style="min-width: 75px; max-width: 75px;">
            <select key={perPage} type="select" bind:value={perPage} on:change={handlePerPageChange}>
              <option value="10">10</option>
              <option value="25">25</option>
              <option value="50">50</option>
              <option value="75">75</option>
              <option value="100">100</option>
            </select>
            </Container>
          </Container>
        </Container>
    {:else}
      <Container class="text-center mt-4">
        <h4>{$_("MovieIdPage.noReviewsFound")}</h4>
        <h4>{$_("MovieIdPage.leaveAReview")}</h4>
      </Container>
    {/if}
    </Container>
    <Container>
      {#each comments as comment, index (`${filmId}-${comment.userId}`)}
        <CommentCard 
          username={comment.userName} 
          commentText={comment.commentText} 
          score={comment.userRating} 
          userId={comment.userId} 
          currentRating={comment.userCommentRating} 
          totalRating={comment.totalCommentRating}
          isCommentDeletable={isCommentDeletable}
          isUsernameRoutable={isUsernameRoutable}
          on:rateComment={handleCommentRate}
          on:deleteComment={handleAdminCommentDelete}
        />
      {/each}
    </Container>
  {/if}
{#if modalOpen}
  <Modal 
    modalTitle={$_("MovieIdPage.rating") + filmDetails?.filmName || ''}
    modalBody={ currentRating ? $_("MovieIdPage.updateCurrentRaiting") + currentRating : $_("MovieIdPage.rateThisMovie")}
    buttonText={$_("MovieIdPage.rate") + rating}
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

  @media (max-width: 530px) {
    :global(.filter-container) {
      flex-direction: column;
      padding: 0;
    }
    :global(.filter-select) {
      width: 200px !important;
      margin-bottom: 10px;
    }
    :global(.filter-select-order) {
      width: 200px !important;
      margin-bottom: 10px;
    }
    :global(.second-info-containter) {
      flex-direction: column !important;
      margin-bottom: 10px;
    }
    :global(.per-page-label) {
      display: block !important;
      text-align: center !important;
    }
    :global(.per-page-panel) {
      flex-direction: column;
    }
  }
</style>