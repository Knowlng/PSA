<script>
    import { onMount } from 'svelte';
    import { page } from '$app/stores';
    import { addToast } from "$lib/ToastNotification/toastStore.js";

    let id = $page.params.id;
    let filmDetails;

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
    
    onMount(() => {
        getMovieInfo();
    });
</script>

<h1>{filmDetails?.filmName || ''}</h1>
<p><strong>Description:</strong> {filmDetails?.filmDesc || ''}</p>
<p><strong>Release Date:</strong> {filmDetails?.filmReleaseDate || '-'}</p>
<p><strong>Rating:</strong> {filmDetails?.filmRating || '-'}</p>
<p><strong>Gross:</strong> ${filmDetails?.filmGross != null ? filmDetails.filmGross : '-'}</p>
{#if filmDetails?.genres && filmDetails.genres.length > 0}
    <p>
    <strong>Genres:</strong>
    {#each filmDetails.genres as genre, i}
        {genre.name}{i < filmDetails.genres.length - 1 ? ', ' : ''}
    {/each}
    </p>
{/if}

{#if filmDetails?.actors && filmDetails.actors.length > 0}
    <p>
    <strong>Actors:</strong>
    {#each filmDetails.actors as actor, i}
        {actor.name} ({actor.role}){i < filmDetails.actors.length - 1 ? ', ' : ''}
    {/each}
    </p>
{/if}
