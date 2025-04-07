<script>
  import { COMMENT_MAX_LENGTH } from '$lib/consts.js';
  import { Container, Input, Label } from '@sveltestrap/sveltestrap';
  import Star from '$lib/Star.svelte';
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let commentText;
  export let username;
  export let score;
  export let currentRating;
  export let totalRating;
  export let userId;

  let rating;

  function handleLike() {
    if (currentRating === true) {
      rating = null;
      currentRating = null;
      totalRating -= 1;
    } else if (currentRating === false) {
      rating = true;
      currentRating = true;
      totalRating += 2;
    } else {
      rating = true;
      currentRating = true;
      totalRating += 1;
    }
    dispatch('rateComment', { rating, userId });
  }

  function handleDislike() {
    if (currentRating === false) {
      rating = null;
      currentRating = null;
      totalRating += 1;
    } else if (currentRating === true) {
      rating = false;
      currentRating = false;
      totalRating -= 2;
    } else {
      rating = false;
      currentRating = false;
      totalRating -= 1;
    }
    dispatch('rateComment', { rating, userId });
  }
</script>

<Container class="d-flex justify-content-center flex-column">
  <Container style="width: 100%; max-width: 700px; border: 1px solid; border-radius: 5px;" class="p-0 mb-4">
    <Container class="p-0 d-flex justify-content-between align-items-center comment-header">
      <p class="ps-2 m-0"><strong>{username}</strong></p>
      <Container class="p-0 d-flex justify-content-end align-items-center">
        <p class="m-0 me-1"><strong>{score}/10</strong></p>
        <Star filled={true} color="blue" size={22}/>
      </Container>
    </Container>
    <Input
      rows={5} type="textarea"
      bind:value={commentText}
      style="resize: none;"
      maxlength={COMMENT_MAX_LENGTH}
      disabled
    />
    <Container class="p-0 comment-footer">
      <Container class="p-0 d-flex justify-content-end align-items-center">
        {#if localStorage.getItem('userLoggedIn') === 'true'}
          <p class="ps-2 m-0"><strong>Like:</strong></p>
          <Star filled={currentRating === true} color="blue" size={32} clickable={true} on:click={handleLike}/>
          <p class="ps-2 m-0"><strong>Dislike:</strong></p>
          <Star filled={currentRating === false} color="red" size={32} clickable={true} on:click={handleDislike}/>
        {/if}
        <p class="ps-2 m-0"><strong>Rating: {totalRating}</strong></p>
        <Star filled={true} color="yellow" size={32}/>
      </Container>
    </Container>
  </Container>
</Container>

<style>
  :global(.comment-header) {
    background-color: var(--amber);
    border-bottom: 1px solid;
  }
  :global(.comment-footer) {
    border-top: 1px solid;
  }
</style>