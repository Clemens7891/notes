package at.fh.swengb.ambrosch


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi


object NotesAPI {
    val retrofitService: NoteApiService
    val retrofit: Retrofit

    init {
        val moshi = Moshi.Builder().build()
        retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://notes.bloder.xyz")
            .build()
        retrofitService = retrofit.create(NoteApiService::class.java)
    }
}

interface NoteApiService {
    @GET("/notes")
    fun getnotes(@Header("access-token") accessToken: String, @Header("last-sync") lastSync: Long):Call<NotesResponse>

    @POST("/login")
    fun login(@Body body: AuthRequest):Call<AuthResponse>

    @POST("/notes")
    fun addOrUpdateNote(@Header("access-token") accessToken: String, @Body body: Note):Call<Note>


}