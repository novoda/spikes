package com.novoda.redditvideos

import com.google.gson.reflect.TypeToken
import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.Thing
import com.novoda.reddit.data.createGson
import com.novoda.redditvideos.model.PreviewUrl
import com.novoda.redditvideos.model.Video

private fun loadResource(path: String): String = VideoFeedViewModelTest::class.java.getResource(path)!!.readText()

val initialPageResults: Listing<Thing.Post> = createGson().fromJson(
    loadResource("/com/novoda/redditvideos/videos_page0.json"),
    object : TypeToken<Listing<Thing.Post>>() {}.type
)

val secondPageResults: Listing<Thing.Post> = createGson().fromJson(
    loadResource("/com/novoda/redditvideos/videos_page1.json"),
    object : TypeToken<Listing<Thing.Post>>() {}.type
)

val expectedInitialListings = listOf(
    Video(
        id = "t3_a4ms86",
        title = "Best made Youtube rewind video was made by Weezer",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/JQeAPFQjQpSs0P4CO6MmedBGzC_3Z-KZ4W6y7amHTaQ.jpg?auto=webp&s=4e96db0cc6796d0189f3f54cafd5109e923bc343")
    ),
    Video(
        id = "t3_a4lqkl",
        title = "Maa!! The Cat is back!",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/Huhq8084u-fKXlfm3l7o-0A-mmI27rvplVLmv6npDjE.jpg?auto=webp&s=898c6f3c2ab3d9e1da3a894d3daf08a3c5887463")
    ),
    Video(
        id = "t3_a4n4yo",
        title = "Rage Against Vanessa Carlton",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/b5zt7od0PqTOwCZJJSL84cedWkqBsK9-nY9omBOH5Fw.jpg?auto=webp&s=4044e75db13ab6ab182d8b9625182bceb14c8e63")
    ),
    Video(
        id = "t3_a4kvdv",
        title = "Do You Speak English?",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/VPTM_DFC0gU3dY4mLHAgNFqHJL97TgeAGA-nEQnkxwY.jpg?auto=webp&s=b30c2dfa530b5a128507db90891d6b7a512da8f6")
    ),
    Video(
        id = "t3_a4gx3t",
        title = "Local news catches scamming tow truck companies and gets state investigators to swarm in realtime for instant justice",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/rGRCIR8fnkmEUMV4pwoDBSPNNoRoZG9TaHLpZpkA4OU.jpg?auto=webp&s=09cad7c85123885c2806ff96850e60b1bb72288d")
    ),
    Video(
        id = "t3_a4nymq",
        title = "An IKEA children's bed converted into a castle with tower and secret passage.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/jcCJhdWMNpqPzKlKXBKppnZ1-PDiHAxaXWSL6wUeDdk.jpg?auto=webp&s=32b435f919b30035d8b366990cc8ded275d0c0d7")
    ),
    Video(
        id = "t3_a4lro6",
        title = "A specialist working on the paint job of brand new Porches. This guy is so rigorous with the details that I never knew I could be so fascinated about a video about car detailing.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/w-BDXDsK5kPNK1lrURaMBhwLGsJwt5Lmu3xPGGLpKng.jpg?auto=webp&s=904199c150cdd0d88ecc977f799b8d37dcb4bb80")
    ),
    Video(
        id = "t3_a4hb3v",
        title = "My fiance bought a big Santa statue last week that startled me in the middle of the night. She didn't think it could be scary since it's Santa.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/83MUa-icUz0wkGgVwJFJ2-J9FRpD13d6567k9Jc7qIs.jpg?auto=webp&s=15eb259e08f837a5569c706480baa792202bb363")
    ),
    Video(
        id = "t3_a4mzjl",
        title = "NINTENDO 64!!!",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/HFb6xLgXYsL370pfaGzJOU1xypkUVi1GW1HnuVfdNa4.jpg?auto=webp&s=2aec3b7787f0dc203a72e738a65db31120a08295")
    ),
    Video(
        id = "t3_a4daaa",
        title = "Amazon fulfilment centres portrayed by South Park",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/LXwCFnvc39CLrJsI0SLGH4Qoa9NnQiGFqM1PEcvItP8.jpg?auto=webp&s=c6e2eb924eabfee8ea87013fd30ee5875727cf30")
    ),
    Video(
        id = "t3_a4l0lf",
        title = "It's that time of the year again: Arson as a Christmas Tradition: The Gävle Goat",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/eX2V2oAEVEnRrNFRjo_cqQVcceQ1cNh5cuzoMlvegTU.jpg?auto=webp&s=fd389fda75d92a2787384bec62806ea7a509b022")
    ),
    Video(
        id = "t3_a4azot",
        title = "Lockpicking Lawyer attempts to break into a bike lock, causes a small explosion.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/LQFy01yl0QoXjqHiGIPRbk8HMgyyvUsCNa75WYaOMLw.jpg?auto=webp&s=7fc8748a35cfe3bbd8a8fbef625bec7d8b130984")
    ),
    Video(
        id = "t3_a4nywj",
        title = "I want to go to there - Tron Cycle coaster at Disneyland Shanghai",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/sa1BX0vf5JC8kX9R9p4SgSNjJP3ng3nJ2koapoFIC84.jpg?auto=webp&s=ad528a053fad1fd6f234ae64c9bdcb75133d24da")
    ),
    Video(
        id = "t3_a4i5mc",
        title = "Man goes 20 years without brushing his teeth, finally goes to the dentist.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/wfmtf3OYBMHuXVKwmWTG1Pd_kt0k1At7_SnmunoRy8M.jpg?auto=webp&s=7665ad8408c1dcf3aed56b726c1ea68fec52fcaa")
    ),
    Video(
        id = "t3_a4owwu",
        title = "Norm as Quentin Tarantino is the greatest thing I've ever seen",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/Gu7oTIIGP3BUJfOWSgwdlsMsWsdLQAB-UrYX1eiTo6Q.jpg?auto=webp&s=172b3249c6f1abf73f32ec60bb380a639874ccf3")
    ),
    Video(
        id = "t3_a4o0cl",
        title = "Moment woman SCREAMS at man when he tries to give her money",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/VxZhocMolGsp17zPruTXwauXlPxkP51nfqXOpzUlZpQ.jpg?auto=webp&s=03ca36408a6b5114d84bb9217f9984485993f366")
    ),
    Video(
        id = "t3_a4mu7j",
        title = "Bozeman, Montana. The Last Best Place",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/8hOCz4tdGhpWsUtosls06aDNW30hjD2OVuDAK17tWzk.jpg?auto=webp&s=f6b5fb5e072fe931c779f0359da0aa0844471274")
    ),
    Video(
        id = "t3_a4nigk",
        title = "Shaun of the Dead - Don’t Stop Me Now (Queen)",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/OetTaQvxk_4DuTIc1_Ud6Ga8fvGn0WJGRicesMFtNbQ.jpg?auto=webp&s=5e05191d80a688002da19ec9ef2e2d6fb9eaa3a6")
    ),
    Video(
        id = "t3_a4ocbi",
        title = "Riff Raff made a hilarious commercial for G FUEL",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/Jq6jnANRFmzCridnA-QNDQa4sB8fSJbmq6LfnzylUok.jpg?auto=webp&s=3e2596f06ec81a1ed63d8401881e5e00146d0945")
    ),
    Video(
        id = "t3_a4lt1h",
        title = "Elf on the Shelf - SNL",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/Mp6FaidEcq7Ih6Wyw4KMk-Dw_0djIlVmqcy_YB8150k.jpg?auto=webp&s=9e2a6dd5f6bc322b14e02131edc65903defbd8c9")
    ),
    Video(
        id = "t3_a4nstr",
        title = "Soldier's letter to his wife before he was killed a week later in the First Battle of Bull Run",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/ST_WOo-t1wRfYXzlsfgWfKE1MTnKGB9Dwzq3S95jEBo.jpg?auto=webp&s=245a8cc65baea8834a244e8656e0fbaed8b886db")
    ),
    Video(
        id = "t3_a4om2i",
        title = "Robin Williams with Elmo on Sesame Street Bloopers",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/CB4SK0xtMxIzlu7Z4wXJuSwUBSig-t-LYBiPB2-5_qk.jpg?auto=webp&s=d341ff4a12cbd721e521ed36402d469a7cc6e609")
    ),
    Video(
        id = "t3_a4difs",
        title = "When Smash Bros Ultimate comes out during final exams",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/EuhYas5r2sYfwFThlMrIeGD5QBANOcIuXZFEqGN-QVU.jpg?auto=webp&s=1bc7806c9f181be24e0283126567fe9e7cb5ac7a")
    ),
    Video(
        id = "t3_a4m30q",
        title = "Incredible multitasking guy who play a bass line with his feet while he is playing a guitar solo with his hands. I've never seen anything like this.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/D3ghO1DqMpslXeG8CKcKvRWVvBhIO0hzxNisqEE7uE0.jpg?auto=webp&s=9bcfafc53a172ca96a219b8fc380cd624b209dd9")
    ),
    Video(
        id = "t3_a4hjzs",
        title = "Pilot shows flat earthers that their app contradicts their model in Hawaii",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/1DFZO7gOv3QS52YeZzRHQIVYuiswmydSHxSejKGkel0.jpg?auto=webp&s=8d88debc69e5caf4ef5504237a63a1a2aec185c7")
    )
)

val expectedSecondListings = listOf(
    Video(
        id = "t3_a4nkht",
        title = "The Bethel College Choir sang in my grain bin! (Down To The River To Pray)",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/txydh8ChZaYhV9ixmnAkFg_xyXekujonzz1VkUiB8D4.jpg?auto=webp&s=cc369b8d4725d467b227e47f114392b8088029c1")
    ),
    Video(
        id = "t3_a4dlmr",
        title = "Donkey bro protecting rancher from cow mom while he attends to the calf",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/DWfnqFGMS5Hv-SNQ7LskptBSK3nL2RUv3Ez-56lxaPI.jpg?auto=webp&s=a728484d9347e175052ad9cc71166b5f276623fb")
    ),
    Video(
        id = "t3_a49ghd",
        title = "Cameraman laughing as an avalanche approaches and engulfs him.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/c9IS6WWX0U85klPZRa_y7cwDRvmJtGf_nCKX9c8kqcE.jpg?auto=webp&s=bd9c1121010b6dc50c88de18ddf3fd44748e54d7")
    ),
    Video(
        id = "t3_a4b29i",
        title = "Crushing Long Steel Pipes with Hydraulic Press | REALLY SATISFYING!",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/PPBwJAAfUkeW0aboe9UTDi3wDA4hJD1eHRbf6nxzbbM.jpg?auto=webp&s=36bfb340a683f784ca96e01996b03c53c8720dac")
    ),
    Video(
        id = "t3_a4jdc2",
        title = "Beat Saber (Eminem - Rap God) Expert+",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/nujEqSpXPOWmD9Kj6hnaXhdMfkiMTPmyxzh4vkIo2rg.jpg?auto=webp&s=f8661762c0310dd75512a6c49412dbaab84a3e86")
    ),
    Video(
        id = "t3_a4nrtu",
        title = "Crack",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/L8T8WJiD5pj3SjwM9KgCIH5QY8qsUd50WKMVwApnw-U.jpg?auto=webp&s=b8a665db657092bed2355d484e77c64f7b75fca2")
    ),
    Video(
        id = "t3_a4p44w",
        title = "Modern packaging, everybody",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/YTP1XNmy8eKqfz7-gw2uSi1Uu_gNS5qFrD2Hstm9Tbk.jpg?auto=webp&s=7befb58d1dd07d8cc1ede2a8c25f45a03fc3c572")
    ),
    Video(
        id = "t3_a4j6jh",
        title = "Cafe in Japan hires paralyzed to work server robot [no subtitles]",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/UTYSwX4_pvK_mQmCT2HD58eYHPf79alw11pZXlCMgVU.jpg?auto=webp&s=7568c77740a052ea49899d9914cbe78a09a00494")
    ),
    Video(
        id = "t3_a4di4w",
        title = "What my mom hears when I explain what was wrong with the car engine",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/7eiMD30CkloGGatnECAgXqjsYltZ5sN8YZo8nQF-zrg.jpg?auto=webp&s=a726bfcb4dfa69a409f798cac67e70cae54e9fcc")
    ),
    Video(
        id = "t3_a4k7ro",
        title = "How To FORCE Your Friends To Exercise! (EXTREME METHODS!)",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/UtialGuiroZ9NMQYOw454zYSLZXXjg90jGWjesD97Ww.jpg?auto=webp&s=5cd04264e29f963bb8fca5fc9a9b8fcb8a2ec7bf")
    ),
    Video(
        id = "t3_a4dtbm",
        title = "Siamese twins",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/PzvwEe4J78DyWYBKHi0ZETKqGvnoLVOt3krChUPUfJs.jpg?auto=webp&s=8366d30f98eaf9e56fcb404ffe432c45162928af")
    ),
    Video(
        id = "t3_a4hvfn",
        title = "Dog miraculously survives Camp Fire, found waiting at site of burned down home",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/p0DxW2-e9PSkJJL6dHut54yMNUtdQz6y6EyAjIypZxg.jpg?auto=webp&s=cfc2eccb92058106d38cb3e40be20647c625d2d9")
    ),
    Video(
        id = "t3_a4okvz",
        title = "TIL that The Lion King, Disney's \"first original story\" is stolen from a 1950s/1960s manga and anime series called Kimba The White Lion.",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/TAVG5GQqPFMnDF8RYgUF69uAcwc-b3P_bVDrux9i-o8.jpg?auto=webp&s=9c7b77127861f8054cd22860eaef8f28f3e3ecee")
    ),
    Video(
        id = "t3_a4kab6",
        title = "Inside a Flat Earth Conference",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/aAbAY1AEw_P2wdICTNpjG0tjoQLB9QZv-ujtKY5FjHk.jpg?auto=webp&s=9fc75c4bb7228b0b31be6fec25b6b95097220367")
    ),
    Video(
        id = "t3_a4g1v9",
        title = "My anime fans know",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/sJjnPDhxWDsTpFvJJ1WYA6s2JKDysmvtQOqYzyuxN7Q.jpg?auto=webp&s=34f9e560b19fe484eea34f3cbd8278019db8b09c")
    ),
    Video(
        id = "t3_a4ovj8",
        title = "goose \"attacks\" kid",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/smQVN6mUKdK4BCnHtP9LgKvWlkOR7ElmXWyvwYPFpH8.jpg?auto=webp&s=f948700569c8b024ba8f6e804f48f5067581e41d")
    ),
    Video(
        id = "t3_a4gi0x",
        title = "Handheld inverted fire tornado bubble trick OC",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/rxTr2ZTtCTH_l9IrCaD_nj4aby_3VFsBmiKg69CCzNI.jpg?auto=webp&s=50e3532d832f164a003520193db9c0c3e38d467b")
    ),
    Video(
        id = "t3_a4leoa",
        title = "If a conspiracy theorist had a movie trailer",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/TERDzwM3mP0Cl3PndBB9559DiVJNpJSgqopo8Vvd9Bc.jpg?auto=webp&s=b6a56855f14e8749d6a1f510d61e24bf6eb5d5f0")
    ),
    Video(
        id = "t3_a4jyz5",
        title = "Stretch Armstrong vs. Waterjet",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/Mz02ryb3SXyXgWUWqhtNTqBrHVqLACFyYRQDehK19XU.jpg?auto=webp&s=4fc44ee9f7a429f5f2565009588679042dbbbac5")
    ),
    Video(
        id = "t3_a4jbv9",
        title = "Home Alone movie as a Flipbook Cartoon",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/48mXAK2_NU0ewojSwEWNuFoqokq3HZNX4hnSTrFM2eg.jpg?auto=webp&s=0a74c808cbcf92677ac3d3ff90f55c27fcde7118")
    ),
    Video(
        id = "t3_a4p8ew",
        title = "Layers",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/FTimU1ca_aiYhomYKcIPJ-E3vATUyz9HA-pbH2H_ofw.jpg?auto=webp&s=fe9a887ead699ffaf027dfe926bb698a9cea47f5")
    ),
    Video(
        id = "t3_a4p8e5",
        title = "In response to the one radio station withdrawing “Baby It’s Cold Outside” because of one complaint, I have a solution. The 1949 film that introduced the song to the world at large didn’t just have the man singing the “Oh Baby” rôle...",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/VNgVawIX-6zkkZI2FPpEDc67lULHoMhkS78hanoDLK4.jpg?auto=webp&s=ba6955d71c5e953346bbd95e2be52aa6b94cf068")
    ),
    Video(
        id = "t3_a4oi3o",
        title = "Was Roman Concrete Better?",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/5Xsk9b21V5l1x-1mEK5cWQzqZUi12IyIiSRKmOtxpIM.jpg?auto=webp&s=808f0e7aa1a3ecf43c9ef5bb113c42b663e2b3fc")
    ),
    Video(
        id = "t3_a4ndf3",
        title = "LEGO TECHNIC Bridge Girder SLJ 50018 ( Final Video 15 )",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/LKN3DJhw90IIPqsrWFNqSnoGAMJwzhqH1eE9g-ciVeU.jpg?auto=webp&s=a5fb344939e7244c6d5485d3a3ff273da6961e52")
    ),
    Video(
        id = "t3_a4p2p1",
        title = "Miami Dolphins miracle touchdown victory against New England Patriots",
        previewUrl = PreviewUrl(value = "https://external-preview.redd.it/InpPKJtxWmCBGAlVWrEWVD7lp78TrKfrvxo9W5nixP4.jpg?auto=webp&s=2e5c8ed745b9e80f2bdc37b372f92fc626bf479a")
    )

)
