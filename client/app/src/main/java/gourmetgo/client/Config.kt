package gourmetgo.client

object AppConfig {

    //Flag to use mockups
    const val USE_MOCKUP = false
    const val API_BASE_URL = "https://gourmet-go-core.vercel.app/api/"
    //Flag to allow login
    const val ENABLE_LOGGING = true
    //Param to config a delay when using mockups in the requests
    const val MOCK_NETWORK_DELAY = 800L //mil seconds

     private const val CLOUD_NAME = "dsr48ffu2"
     const val UPLOAD_PRESET = "gourmetgo_users"
     const val CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
}