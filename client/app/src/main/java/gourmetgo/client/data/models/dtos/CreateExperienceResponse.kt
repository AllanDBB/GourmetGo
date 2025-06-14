package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Experience

data class CreateExperienceResponse(
    val message: String,
    val experience: Experience
) 