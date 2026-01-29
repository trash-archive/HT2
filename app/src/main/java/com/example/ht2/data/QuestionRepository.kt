package com.example.ht2.data

import kotlinx.coroutines.flow.first

class QuestionRepository(private val dataStoreManager: DataStoreManager) {

    // Start custom question IDs at 1000 to avoid conflicts with preset questions
    private var nextCustomId = 1000

    private val presetQuestions = listOf(
        // Memories (1–15)
        Question(1, "What's your favorite memory of us together?", "Memories"),
        Question(2, "What was the first moment you felt close to me?", "Memories"),
        Question(3, "What's the funniest thing we've experienced together?", "Memories"),
        Question(4, "What's a small moment with me you'll never forget?", "Memories"),
        Question(5, "What was our best date so far?", "Memories"),
        Question(6, "What memory of us always makes you smile?", "Memories"),
        Question(7, "What was the most unexpected thing we went through together?", "Memories"),
        Question(8, "What moment made you feel proud of us?", "Memories"),
        Question(9, "What's your favorite late-night memory with me?", "Memories"),
        Question(10, "What's a moment you wish we could relive?", "Memories"),
        Question(11, "What memory shows how far we've come?", "Memories"),
        Question(12, "What was a turning point in our relationship?", "Memories"),
        Question(13, "What memory reminds you why you chose me?", "Memories"),
        Question(14, "What's the sweetest thing we've done together?", "Memories"),
        Question(15, "What's a quiet moment with me you treasure?", "Memories"),

        // Love (16–30)
        Question(16, "What do you love most about our relationship?", "Love"),
        Question(17, "When do you feel most loved by me?", "Love"),
        Question(18, "What's your favorite way I show you love?", "Love"),
        Question(19, "What makes you feel safe with me?", "Love"),
        Question(20, "How do you know I care about you?", "Love"),
        Question(21, "What does love mean to you right now?", "Love"),
        Question(22, "What's one thing I do that makes you feel special?", "Love"),
        Question(23, "What makes our love different from others?", "Love"),
        Question(24, "How has being with me changed how you love?", "Love"),
        Question(25, "What's your favorite thing about loving me?", "Love"),
        Question(26, "When do you feel closest to me?", "Love"),
        Question(27, "What part of our love do you protect the most?", "Love"),
        Question(28, "What makes you choose me every day?", "Love"),
        Question(29, "What kind of love do you feel when you're with me?", "Love"),
        Question(30, "What makes you feel emotionally connected to me?", "Love"),

        // Sweet (31–45)
        Question(31, "What song reminds you of me?", "Sweet"),
        Question(32, "What's something that always makes you think of me?", "Sweet"),
        Question(33, "What's your favorite thing about my smile?", "Sweet"),
        Question(34, "What's something cute I do without realizing it?", "Sweet"),
        Question(35, "What's your favorite photo of us?", "Sweet"),
        Question(36, "What do you miss about me when we're apart?", "Sweet"),
        Question(37, "What's your favorite way we spend time together?", "Sweet"),
        Question(38, "What's a habit of mine you secretly love?", "Sweet"),
        Question(39, "What's something I do that instantly cheers you up?", "Sweet"),
        Question(40, "What's one word that describes how you feel about me?", "Sweet"),
        Question(41, "What makes you smile when you think of us?", "Sweet"),
        Question(42, "What's your favorite thing I say to you?", "Sweet"),
        Question(43, "What's a moment when you felt truly appreciated by me?", "Sweet"),
        Question(44, "What's your favorite way I show affection?", "Sweet"),
        Question(45, "What's something small about me you love deeply?", "Sweet"),

        // Future (46–60)
        Question(46, "What's your biggest dream for our future?", "Future"),
        Question(47, "Where do you see us in 5 years?", "Future"),
        Question(48, "What kind of life do you want us to build together?", "Future"),
        Question(49, "What's a goal we should achieve as a couple?", "Future"),
        Question(50, "What tradition should we start together?", "Future"),
        Question(51, "What kind of home do you imagine for us?", "Future"),
        Question(52, "What do you look forward to experiencing with me?", "Future"),
        Question(53, "What's something important you want us to plan together?", "Future"),
        Question(54, "What values do you want us to share long-term?", "Future"),
        Question(55, "What does growing old together mean to you?", "Future"),
        Question(56, "What kind of future moments excite you the most?", "Future"),
        Question(57, "What do you want our future to feel like?", "Future"),
        Question(58, "What's one promise you want us to keep?", "Future"),
        Question(59, "What dream of yours do you want me beside you for?", "Future"),
        Question(60, "What future version of us makes you happiest?", "Future"),

        // Deep (61–75)
        Question(61, "What's something you've learned about yourself from loving me?", "Deep"),
        Question(62, "What makes our relationship strong during hard times?", "Deep"),
        Question(63, "What's something you want us to understand better about each other?", "Deep"),
        Question(64, "What does commitment mean to you?", "Deep"),
        Question(65, "How do we help each other grow?", "Deep"),
        Question(66, "What's something you admire about how we handle challenges?", "Deep"),
        Question(67, "What's something important you want us to protect?", "Deep"),
        Question(68, "What do you think keeps our bond strong?", "Deep"),
        Question(69, "What's a fear you feel safe sharing with me?", "Deep"),
        Question(70, "What's something you want us to work on together?", "Deep"),
        Question(71, "How do you feel supported by me?", "Deep"),
        Question(72, "What's a lesson our relationship has taught you?", "Deep"),
        Question(73, "What helps us reconnect after misunderstandings?", "Deep"),
        Question(74, "What does emotional safety mean to you?", "Deep"),
        Question(75, "What's something you trust me with completely?", "Deep"),

        // Fun (76–90)
        Question(76, "If we had a theme song, what would it be?", "Fun"),
        Question(77, "If we were characters in a movie, what genre would it be?", "Fun"),
        Question(78, "What food best represents us as a couple?", "Fun"),
        Question(79, "What's something silly only we understand?", "Fun"),
        Question(80, "If we could switch lives for a day, what would surprise you?", "Fun"),
        Question(81, "If we had a couple nickname, what would it be?", "Fun"),
        Question(82, "What fictional couple are we most like?", "Fun"),
        Question(83, "What's the weirdest thing we've laughed about together?", "Fun"),
        Question(84, "If we had a reality show, what would it be called?", "Fun"),
        Question(85, "What's a random habit we share?", "Fun"),
        Question(86, "If we could time travel together, where would we go?", "Fun"),
        Question(87, "What would our couple emoji be?", "Fun"),
        Question(88, "If we owned a café together, what would we name it?", "Fun"),
        Question(89, "What's something spontaneous we should do someday?", "Fun"),
        Question(90, "What inside joke still makes you laugh?", "Fun"),

        // Adventure (91–105)
        Question(91, "What adventure should we go on next?", "Adventure"),
        Question(92, "If we could travel anywhere together, where would it be?", "Adventure"),
        Question(93, "What's something new you want us to try together?", "Adventure"),
        Question(94, "What place do you most want to explore with me?", "Adventure"),
        Question(95, "What's a dream trip you want us to take?", "Adventure"),
        Question(96, "What spontaneous trip should we take someday?", "Adventure"),
        Question(97, "What adventure would push us out of our comfort zone?", "Adventure"),
        Question(98, "What's an experience you want us to remember forever?", "Adventure"),
        Question(99, "What kind of adventure makes you feel alive with me?", "Adventure"),
        Question(100, "What's something exciting we haven't done yet?", "Adventure"),
        Question(101, "What challenge would you want us to face together?", "Adventure"),
        Question(102, "What place feels romantic to explore with me?", "Adventure"),
        Question(103, "What adventure scares you a little but excites you?", "Adventure"),
        Question(104, "What outdoor activity would you love to do with me?", "Adventure"),
        Question(105, "What journey do you want us to start together?", "Adventure"),

        // Us / Appreciation (106–120)
        Question(106, "What makes us different from other couples?", "Us"),
        Question(107, "What's something you appreciate about me today?", "Us"),
        Question(108, "What do you never want to lose between us?", "Us"),
        Question(109, "What promise would you make to our relationship?", "Us"),
        Question(110, "What makes you proud to be with me?", "Us"),
        Question(111, "What's something small I do that matters a lot to you?", "Us"),
        Question(112, "What does 'us' mean to you?", "Us"),
        Question(113, "What's something you're grateful for about us right now?", "Us"),
        Question(114, "What's a strength we have as a couple?", "Us"),
        Question(115, "What makes our connection special?", "Us"),
        Question(116, "What's something you want us to protect no matter what?", "Us"),
        Question(117, "What makes you feel secure in our relationship?", "Us"),
        Question(118, "What's something about me you appreciate more over time?", "Us"),
        Question(119, "What part of our relationship are you most thankful for?", "Us"),
        Question(120, "What's one thing you hope we always remember about each other?", "Us"),

        // Fun & Quirky (Add-ons)
        Question(121, "If we were stuck in an elevator together, what would we do first?", "Fun"),
        Question(122, "Who would survive longer in a zombie apocalypse: me or you?", "Fun"),
        Question(123, "If we had to switch bodies for a day, what would be the hardest part?", "Fun"),
        Question(124, "What's the weirdest habit of mine that you secretly find cute?", "Fun"),
        Question(125, "If we were a meme, what would the caption be?", "Fun"),
        Question(126, "What food do you think I eat in the weirdest way?", "Fun"),
        Question(127, "If we opened a YouTube channel together, what would it be about?", "Fun"),
        Question(128, "What's something silly you'd never do with anyone else but me?", "Fun"),
        Question(129, "If we had to wear matching outfits forever, what would they look like?", "Fun"),
        Question(130, "Who would win in an argument with a toddler: me or you?", "Fun"),
        Question(131, "If we were animals, what animals would we be?", "Fun"),
        Question(132, "What's my most dramatic reaction to something small?", "Fun"),
        Question(133, "If we were characters in a sitcom, who would be the funny one?", "Fun"),
        Question(134, "What's the most chaotic thing we've ever done together?", "Fun"),
        Question(135, "If we made a couple emoji, what would it be?", "Fun"),
        Question(136, "What's something I do that makes you say \"this is so us\"?", "Fun"),
        Question(137, "If we had to compete on a reality show, which one would we win?", "Fun"),
        Question(138, "What would our couple catchphrase be?", "Fun"),
        Question(139, "If we had a theme song but it had to be embarrassing, what would it be?", "Fun"),
        Question(140, "What's a silly argument we'd probably laugh about later?", "Fun"),
        Question(141, "If we owned a café together, what weird drink would we invent?", "Fun"),
        Question(142, "What's something small I do that always makes you laugh?", "Fun"),
        Question(143, "If we were villains, what would our evil plan be?", "Fun"),
        Question(144, "What's a random inside joke that still cracks you up?", "Fun"),
        Question(145, "If we had to live in one app forever, which app would we choose?", "Fun"),
        Question(146, "Who would be more likely to forget an anniversary (be honest 😏)?", "Fun"),
        Question(147, "If we went viral for something, what do you think it would be?", "Fun"),
        Question(148, "What's the funniest misunderstanding we've had?", "Fun"),
        Question(149, "If we were in a cartoon, what would our personalities be?", "Fun"),
        Question(150, "What's the most unserious plan we've ever made?", "Fun"),
        Question(151, "If we could prank each other once without consequences, what would you do?", "Fun"),
        Question(152, "What's something weird we both enjoy?", "Fun"),
        Question(153, "If we had a secret handshake, what would it include?", "Fun"),
        Question(154, "What's one thing I do that would totally give me away in a disguise?", "Fun"),
        Question(155, "If we had to rename ourselves, what names would we pick?", "Fun"),
        Question(156, "What's something we do that would confuse other people?", "Fun"),
        Question(157, "If we were emojis in a text, which ones would we be?", "Fun"),
        Question(158, "What's a moment we shouldn't have laughed at but did anyway?", "Fun"),
        Question(159, "If we had a couple mascot, what would it be?", "Fun"),
        Question(160, "What's the most ridiculous date idea you'd still say yes to?", "Fun")
    )

    // Mutable list to store custom questions
    private val _customQuestions = mutableListOf<Question>()

    // Initialize repository with saved data
    suspend fun initialize() {
        // Load custom questions
        val savedCustomQuestions = dataStoreManager.getCustomQuestions().first()
        _customQuestions.clear()
        _customQuestions.addAll(
            savedCustomQuestions.map {
                Question(it.id, it.text, "My Questions", isCustom = true)
            }
        )

        // Load next custom ID
        nextCustomId = dataStoreManager.getNextCustomId().first()
    }

    fun getRandomQuestion(): Question = getAllQuestions().random()

    fun getAllQuestions(): List<Question> = presetQuestions + _customQuestions

    fun getQuestionsByCategory(category: String): List<Question> {
        return if (category == "All") {
            getAllQuestions()
        } else if (category == "My Questions") {
            _customQuestions
        } else {
            getAllQuestions().filter { it.category == category }
        }
    }

    fun getAllCategories(): List<String> {
        val baseCategories = listOf("All") + presetQuestions.map { it.category }.distinct().sorted()
        return if (_customQuestions.isNotEmpty()) {
            baseCategories + "My Questions"
        } else {
            baseCategories
        }
    }

    suspend fun addCustomQuestion(text: String): Question {
        val newQuestion = Question(
            id = nextCustomId++,
            text = text,
            category = "My Questions",
            isCustom = true
        )
        _customQuestions.add(newQuestion)

        // Save to DataStore
        dataStoreManager.saveCustomQuestions(_customQuestions)
        dataStoreManager.saveNextCustomId(nextCustomId)

        return newQuestion
    }

    suspend fun deleteCustomQuestion(questionId: Int) {
        _customQuestions.removeAll { it.id == questionId }

        // Save to DataStore
        dataStoreManager.saveCustomQuestions(_customQuestions)
    }

    fun getCustomQuestions(): List<Question> = _customQuestions.toList()
}