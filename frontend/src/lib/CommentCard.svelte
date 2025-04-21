<script>
  import { COMMENT_MAX_LENGTH } from '$lib/consts.js';
  import { Container, Input, Label } from '@sveltestrap/sveltestrap';
  import Star from '$lib/Star.svelte';
  import { createEventDispatcher } from 'svelte';
  import Modal from '$lib/Modal.svelte';
  import { _ } from "svelte-i18n";

  const dispatch = createEventDispatcher();

  export let commentText;
  export let username;
  export let score;
  export let currentRating;
  export let totalRating;
  export let userId;
  export let isCommentDeletable;
  export let isUsernameRoutable = false;

  let rating;
  let modalOpen = false;

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

  function handleCommentDelete() {
    dispatch('deleteComment', { userId });
  }

  function handleUsernameClick() {
    window.location.href = `/manage/manageUsers?userId=${encodeURIComponent(userId)}&username=${encodeURIComponent(username)}`;
  }
</script>

<Container class="d-flex justify-content-center flex-column">
  <Container style="width: 100%; max-width: 700px; border: 1px solid; border-radius: 5px;" class="p-0 mb-4">
    <Container class="p-0 d-flex justify-content-between align-items-center comment-header">
      {#if isUsernameRoutable}
        <button on:click={handleUsernameClick}>{username}</button>
      {:else}
        <p class="ps-2 m-0"><strong>{username}</strong></p>
      {/if}
      <Container class="p-0 d-flex justify-content-end align-items-center">
        <p class="m-0 me-1"><strong>{score ? score : '-'}/10</strong></p>
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
      <Container class="p-0 d-flex sub-container">
        {#if isCommentDeletable}
          <Container class="d-flex justify-content-start align-items-center">
            <button on:click={()=> modalOpen = true}>{$_("CommentCard.delete")}</button>
          </Container>
        {/if}
        <Container class="p-0 d-flex justify-content-end align-items-center sub-container">
          {#if localStorage.getItem('userLoggedIn') === 'true'}
            <p class="ps-2 m-0"><strong>{$_("CommentCard.like:")}</strong></p>
            <Star filled={currentRating === true} color="blue" size={32} clickable={true} on:click={handleLike}/>
            <p class="ps-2 m-0"><strong>{$_("CommentCard.dislike:")}</strong></p>
            <Star filled={currentRating === false} color="red" size={32} clickable={true} on:click={handleDislike}/>
          {/if}
          <p class="ps-2 m-0"><strong>{$_("CommentCard.rating:")}{totalRating}</strong></p>
          <Star filled={true} color="yellow" size={32}/>
        </Container>
      </Container>
    </Container>
  </Container>
</Container>
{#if modalOpen}
  <Modal 
    modalTitle={$_("CommentCard.deleteModalTextStart") + username + $_("CommentCard.deleteModalTextEnd")}
    modalBody={$_("CommentCard.deleteModalBody")}
    buttonText={$_("CommentCard.deleteModalButtonText")}
    on:toggle={() => { modalOpen = false; rating = ''; }}
    on:confirm={handleCommentDelete}
  />
{/if}
<style>
  :global(.comment-header) {
    background-color: var(--amber);
    border-bottom: 1px solid;
  }
  :global(.comment-footer) {
    border-top: 1px solid;
  }

  button {
    all: unset;
    cursor: pointer;
    text-decoration: underline;
  }

  button:hover {
    color: var(--information);
  }

  @media (max-width: 530px) {
    :global(.sub-container) {
      flex-direction: column;
    }
  }
</style>