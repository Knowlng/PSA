<script>
    import { COMMENT_MAX_LENGTH } from '$lib/consts.js';
    import { Container, Input, Label } from '@sveltestrap/sveltestrap';
    import Star from '$lib/Star.svelte';
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    export let commentText;
    export let username;
    export let score;
    export let isLiked;
    export let totalLikes;

    function handleLikeToggle() {
        isLiked = !isLiked;
        dispatch('likeToggle', { isLiked });
    };
</script>


<Container class="d-flex justify-content-center flex-column">
    <Container style="width: 100%; max-width: 700px; border: 1px solid; border-radius: 5px;" class='p-0 mb-4'>
        <Container class='p-0 d-flex justify-content-between align-items-center comment-header'>
            <p class="ps-2 m-0"><strong>{username}</strong></p>
            <Container class="p-0 d-flex justify-content-end align-items-center">
                <p class='m-0 me-1'><strong>{score}/10</strong></p>
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
        <Container class='p-0 comment-footer'>
            <Container class="p-0 d-flex justify-content-end align-items-center">
                <p class="ps-2 m-0"><strong>Liked:</strong></p>
                <Star filled={isLiked ? true : false} color="blue" size={32} clickable={true} on:click={handleLikeToggle}/>
                <p class="ps-2 m-0"><strong>Total likes:</strong></p>
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