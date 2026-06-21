package com.example.data

object CategoriesData {
    val predefinedCategories = listOf(
        Category(
            name = "Wild Animals",
            description = "Creatures of the savage jungle, vast savannas, and deep wilderness.",
            words = listOf(
                "Lion", "Tiger", "Elephant", "Giraffe", "Monkey", "Zebra", "Kangaroo", "Panda",
                "Cheetah", "Gorilla", "Hippopotamus", "Rhinoceros", "Koala", "Sloth", "Wolf", "Bear"
            )
        ),
        Category(
            name = "Delectable Foods",
            description = "Mouth-watering dishes, global delicacies, and delicious desserts.",
            words = listOf(
                "Pizza", "Sushi", "Hamburger", "Pasta", "Taco", "Salad", "Pancake", "Soup",
                "Steak", "Ice Cream", "Chocolate", "Cheese", "Sandwich", "Waffle", "Hotdog", "Donut"
            )
        ),
        Category(
            name = "Global Destinations",
            description = "Famous tourist nations, historical countries, and beautiful places.",
            words = listOf(
                "USA", "Canada", "Japan", "United Kingdom", "France", "Germany", "Italy", "China",
                "Australia", "Brazil", "India", "Egypt", "Russia", "Spain", "Mexico", "Switzerland"
            )
        ),
        Category(
            name = "Cinematic Masterpieces",
            description = "Iconic blockbusters, beloved classics, and giant screen adventures.",
            words = listOf(
                "Titanic", "Inception", "Avatar", "The Matrix", "Gladiator", "Frozen", "Avengers", "Batman",
                "Shrek", "Jaws", "Dracula", "Star Wars", "Toy Story", "Alien", "Dune", "Jumanji"
            )
        ),
        Category(
            name = "Modern Gadgets",
            description = "High-tech hardware, electronic utilities, and everyday computing items.",
            words = listOf(
                "iPhone", "Laptop", "Smartwatch", "Camera", "Drone", "VR Headset", "Router", "Tablet",
                "Headphones", "Keyboard", "Mouse", "Console", "Monitor", "Printer", "Speaker", "USB Drive"
            )
        ),
        Category(
            name = "Prestigious Careers",
            description = "Important jobs, classic occupations, and professional callings.",
            words = listOf(
                "Doctor", "Teacher", "Engineer", "Chef", "Pilot", "Firefighter", "Artist", "Lawyer",
                "Farmer", "Actor", "Nurse", "Scientist", "Astronaut", "Police Officer", "Writer", "Politician"
            )
        )
    )

    /**
     * Local fallback clues for AI bots if internet is missing.
     * Maps secret words to standard simple clues to guarantee flawless gameplay offline!
     */
    fun getLocalFallbackClue(secretWord: String, isChameleonBeforeMe: Boolean): String {
        return when (secretWord.lowercase()) {
            // Animals
            "lion" -> "mane"
            "tiger" -> "stripes"
            "elephant" -> "trunk"
            "giraffe" -> "neck"
            "monkey" -> "banana"
            "zebra" -> "stripes"
            "kangaroo" -> "pouch"
            "panda" -> "bamboo"
            "cheetah" -> "fast"
            "gorilla" -> "jungle"
            "hippopotamus" -> "river"
            "rhinoceros" -> "horn"
            "koala" -> "eucalyptus"
            "sloth" -> "slow"
            "wolf" -> "howl"
            "bear" -> "honey"

            // Foods
            "pizza" -> "slice"
            "sushi" -> "raw"
            "hamburger" -> "bun"
            "pasta" -> "sauce"
            "taco" -> "shell"
            "salad" -> "green"
            "pancake" -> "syrup"
            "soup" -> "bowl"
            "steak" -> "meat"
            "ice cream" -> "cold"
            "chocolate" -> "sweet"
            "cheese" -> "yellow"
            "sandwich" -> "bread"
            "waffle" -> "grid"
            "hotdog" -> "mustard"
            "donut" -> "hole"

            // Countries
            "usa" -> "states"
            "canada" -> "maple"
            "japan" -> "sushi"
            "united kingdom" -> "london"
            "france" -> "paris"
            "germany" -> "berlin"
            "italy" -> "rome"
            "china" -> "wall"
            "australia" -> "sydney"
            "brazil" -> "forest"
            "india" -> "taj"
            "egypt" -> "pyramid"
            "russia" -> "moscow"
            "spain" -> "madrid"
            "mexico" -> "taco"
            "switzerland" -> "alps"

            // Movies
            "titanic" -> "ship"
            "inception" -> "dream"
            "avatar" -> "blue"
            "the matrix" -> "pill"
            "gladiator" -> "arena"
            "frozen" -> "snow"
            "avengers" -> "assemble"
            "batman" -> "gotham"
            "shrek" -> "swamp"
            "jaws" -> "shark"
            "dracula" -> "vampire"
            "star wars" -> "jedi"
            "toy story" -> "sheriff"
            "alien" -> "space"
            "dune" -> "sand"
            "jumanji" -> "boardgame"

            // Tech / Gadgets
            "iphone" -> "phone"
            "laptop" -> "computer"
            "smartwatch" -> "wrist"
            "camera" -> "lens"
            "drone" -> "rotor"
            "vr headset" -> "virtual"
            "router" -> "wifi"
            "tablet" -> "screen"
            "headphones" -> "sound"
            "keyboard" -> "keys"
            "mouse" -> "click"
            "console" -> "gaming"
            "monitor" -> "display"
            "printer" -> "ink"
            "speaker" -> "volume"
            "usb drive" -> "files"

            // Jobs
            "doctor" -> "stethoscope"
            "teacher" -> "classroom"
            "engineer" -> "blueprint"
            "chef" -> "kitchen"
            "pilot" -> "airplane"
            "firefighter" -> "hydrant"
            "artist" -> "canvas"
            "lawyer" -> "courtroom"
            "farmer" -> "tractor"
            "actor" -> "oscar"
            "nurse" -> "hospital"
            "scientist" -> "laboratory"
            "astronaut" -> "shuttle"
            "police officer" -> "badge"
            "writer" -> "notebook"
            "politician" -> "ballot"

            else -> "generic"
        }
    }

    /**
     * Local fallback clues for Chameleon if internet is missing.
     * Looks at other clues and produces something generic or copied to blend in!
     */
    fun getChameleonFallbackClue(existingClues: List<String>): String {
        val candidates = listOf("thing", "object", "popular", "famous", "common", "nice", "big", "small")
        if (existingClues.isNotEmpty()) {
            // Echo a random clue or synonyms
            val matching = existingClues.filter { it.length > 3 && it != "generic" }
            if (matching.isNotEmpty()) {
                val base = matching.random()
                return when (base.lowercase()) {
                    "mane" -> "wild"
                    "stripes" -> "pattern"
                    "trunk" -> "large"
                    "neck" -> "tall"
                    "banana" -> "fruit"
                    "pouch" -> "fur"
                    "slice" -> "food"
                    "raw" -> "ocean"
                    "green" -> "nature"
                    "cold" -> "sweet"
                    "ship" -> "water"
                    "blue" -> "sky"
                    "keys" -> "input"
                    else -> candidates.random()
                }
            }
        }
        return candidates.random()
    }
}
