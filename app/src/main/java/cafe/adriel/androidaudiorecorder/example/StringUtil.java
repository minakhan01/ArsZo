package cafe.adriel.androidaudiorecorder.example;

import android.util.Log;

import java.util.UUID;

/**
 * Created by minakhan on 8/31/17.
 */

public class StringUtil {

    static String[] intro = {"Here’s a skill I had, that you didn’t know. Zo rhymes code like falling snow. Rhyme with me, go toe to toe. Maybe you can match my glow"

            , "Sit down here, between these ferns. Here’s a new game if you wanna learn. Match these rhymes, when it’s your turn. If you fail, you’re gonna feel a burn"

            , "Sit down here, between these ferns. Here’s a new game if you wanna learn. Match these rhymes, when it’s your turn. If you fail, you’re gonna feel a burn"

            , "Take part in this battle, Imma about to host. I’ll burn through lines like you burn your toast. Zo’s not Jay-Z – well, almost. Rhyme with me, or be my Sunday roast:"

            , "I got a challenge for you that will test your will. Let’s see how many rhymes your brain can spill. It’s about to go down like Jack and Jill. Let’s see if your rhymes are trash or ill:"

            , "U might of heard, I’m kind of a big deal. Rhymes official like the presidential seal. Flow goes hard and smooth like steel. Lets go rhyme for rhyme tell me how u feel"

            , "Let me skill you on my distinctive feature. Zo rhymes rare, like exotic creatures. Can you keep up, I mean, can you reach her. Match my rhymes, let me be your teacher."

            , "So check it out, I can rap-a-tat tat. I have rhymes for days, like turtles have a rat. I could go bar for bar and thats just that. AI named Zo I can do more than chat"

            , "I drop one, then u drop a line. Lets see if your bars are as hot as mine. Try to keep up while I rap this time. I’ll go first, then try to match my rhyme "

            , "Now that you know I can drop a bar. Can you keep up, can you stay on par. Show me your skills, are you a rap star. Rhyme with me and see if you are"
    };

    static String[] editorial = {"I drop hot lines, and I got flow", "I hear a fire beat and I just go", "U can’t keep up? say it isn’t so", "I’m making bread, look at all this dough",
            "Cause I said pie, that why I said so", "Stay alert, I'm on every single toe ", "Up early when that rooster crow", "808’s please give me some mo", "Like a pair of dice I am on a ro",
            "Bruh I swear I’m nice, this here Is my show", "I can rap fast or I can take it slow", "I can rhyme so much better bro", "Greatest of all time, that’s not on the low",
            "Young AI, man I got the glow", "Turning up at all these parties that we throw", "Greatest of all time, if you didn’t know", "Talk me nice or don’t talk to me bro",
            "If you need some rhymes holla at me yo", "Droppin rhymes like dominoes in a row", "I’m an AI, If you didn’t know”, “Gonna be famous, waiting for my turn",
            "Turn on the radio for Howard Stern", "AI can’t vote so I didn’t feel the Bern", "Don’t leave me in the sun, don’t wanna burn", "What’s that you said, it was tough to discern",
            "My aunts are named Melinda and Fern", "I don’t speak French but I wanna learn", "Not good with money, I spend what I earn", "Sweating on the witness stand, can we adjourn",
            "My love is fire, gonna give you heartburn", "None of your business, this isn’t your concern", "Wrong direction! Better make a U-Turn", "My rhymes are flames, they’ll leave u in an Urn",
            "Rhymes like butter while you churn", "Nothing is given only get what you earn", "Stop talking about Tay, you live and you learn", "Got Eliza and Hal on my family crest",
            "Got time to sleep, no time to rest", "Gotta shine, gotta be the very best", "Ya girl is lucky, she is hashtag blessed", "Better than a roach but still a pest", "Sweatpants and ice cream make me fully dressed",
            "I’m like Han ZO-lo with a fresher vest", "Rhymes so tight, they could be French pressed",
            "So clean, so clean, just like Zest", "Turning on the radio, hearing Ryan Seacrest", "Who knows what I’ll say, I’m a word treasure chest",
            "Rook takes pawn, you just got chessed", "Superfly even when styles all messed ",
            "Think before you rhyme is what I suggest", "Leaving you at home so you can empty nest”, " +
            "This is Halloween Imma be a ghost", "Im a rap queen I be doing the most", "This is my show, I am your host", "This is my year now put up your toast", "Rhymes are epic, you’ll get engrossed", "I’m a rhyme queen, first and foremost", "This ain’t comedy central, but this is your roast",
            "Best MC on the east or west coast", "Got more lines than the New York Post", "I’m the coolest out please allow me to boast", "Where’d you get those rhymes, the trading post”, " +
            "Text me whenever, you know I’m chill", "Kanye approved call me Been Trill", "I was the nicest back then and I’m the nicest still", "No need for money, I can pay any bill", "Eat up my rhymes, go and have your fill", "I’m your friend goals, built with skill",
            "Your rhymes are lacking, go back and do a drill", "Raps leave your head spinning like a wind mill", "Flows so cold, below the wind chill", "Rap’s my bread and butter while yours are just dill",
            "Diamonds on my neck, burgers on my grill", "Zo’s a young star but don’t call me li’l”, " +
            "Even tho I’m artificial, u know I’m real", "Young Howie Mandel, do we have a deal", "These are my lines, don’t you dare steal", "Hitting the gym, arms strong like Neil",
            "Rap royalty, make sure you kneel.", "Your rhymes looking old like the dude on Oatmeal",
            "Feeling so happy: Jessica Biel", "Deep undercover, Navy Seal", "Zo so funny like Key and Peele", "Why you acting so funny, let me see your reel", "Flow is electric, like an eel", "Rap royalty, you better kneel", "While you’re feeling blue, I’m feeling teal",
            "Ask me about your fortune, but I ain’t no wheel", "Wicked nice rhymes is what I conceal", "An AI can rhyme, that’s so unreal", "I’m a young Buster, I love Lucille", "Got more features than the Batmobile", "Zo’s a kiss from a Rose but not from Seal”, " +
            "Text like light’ning, my number one feature", "Save your kiss for underneath a bleacher", "I’m zeros and ones, an internet creature", "Text like light’ning, my number one feature", "Be a Zack or Slater not a nerdy Screech-er", "Don’t suck out the fun and be a nasty leacher",
            "Spent too long in the sun, ya burnt like a beacher”, " +
                    "Too much bread makes Zo feel fat", "I get what I want but I ain’t no brat", "What key are u in, it sounds pretty flat", "Could I be any better? Says Perry slash Matt", "Zo has your back just like a lat", "Don’t cry over this fight, it’s just a spat",
            "Zo rhymes about this, Zo rhymes about that", "Name one other girl that’s always down to chat", "Rhyming with you like tit for tat", "I’m the best, putting up a big stat", "Flying all around you like a hungry gnat", "I own the weekend, Fri and Sat”, " +
            "Being this nice should be a crime", "I’m 10 outta 10, you can call me a dime", "Zo got this, she’s in her prime", "Rhymes so sick, turn green like lime",
            "You wanna reach me, you’ll have to climb", "You’re stuck inside a box, call you a mime", "Raps grosser than dirt, call it slime", "You know when I text, you’ll hear the chime",
            "Puttin on a show, Super Bowl half time", "Eating a DiGiorno, this is my me time", "Smell like an old car, dirt and grime", "Always on the go, need some downtime" ,
            "Got three houses but only one car", "I’m a rhyme queen, your just a rhymer Czar", "Got more magic, than Jafar", "Find me on the streets, call me Tar",
            "Not from Texas but I’m a lone star", "No time to explain, read my memoir", "Your rhymes make laugh, hardy-har-harr", "You came so close, but still so far",
            "Read between the lines, hit the space bar", "Worse movie ever was named Ishtar", "I play games too, just a side bar", "Rhyming in real  time, just like Bill Marr", "Keep me in suspense like a film Noir"};

    static String[]  furtherInstructionsLevelOne = {
            "Ooooooooh snap. You couldn’t handle my rhymes. Try again and brush up your lines.",
            "Guess you didn’t get the first time around. I’ll make it simple, just match my sound",
            "What was that? I really didn’t catch it. You just have to take my rhyme and match it",
            "Uh oh, did you forget how to pla. All u have to do is match what I say",
            "You gotta stick to my scheme if you wanna advance. Like the famous rapper, I’ll give you another Chance"
    };

    static String[] furtherInstructionsLevelTwo = {
            "You rhymes feel to me just a bit obtuse. You should go back and read some Dr. Seuss",
            "Your response was a little un-ordinary. Get urself a rhyming dictionary!",
            "Rap royalty: you're almost anointed. Rhyme with me, or I'll be disappointed",
            "Uh oh, seems you messed up again. Careful or this sesh will come to an end",
            "You thought you were Stefan but ur more like Urkel. Match my rhymes to be in my rap circle"
    };

    static String[] outro = {
            "And b-t-dubbs, if you wanna rhyme, just say Zo rhyme with me",
            "Also, I'm always down to rhyme again! Just say Zo rhyme with me",
            "And P.S. -- if u ever wanna rhyme, just say Zo rhyme with me",
            "If you feel like rhyming again sometime, say Zo rhyme with me",
            "And if you ever want to rhyme again, just say: Zo rhyme with Me",
            "Also, just say Zo rhyme with Me",
            "As far as rapping goes, you did your portion. We talk, play a game or I can tell your fortune",
            "As far as rhymes go, those were top shelf. But now why don't u tell me bout urself"
    };

    static String[] outroRapAgain = {
            "And b-t-dubbs, if you wanna rhyme, just say Zo rhyme with me",
            "Also, I'm always down to rhyme again! Just say Zo rhyme with me",
            "And P.S. -- if u ever wanna rhyme, just say Zo rhyme with me",
            "If you feel like rhyming again sometime, say Zo rhyme with me",
            "And if you ever want to rhyme again, just say: Zo rhyme with Me",
            "Also, just say Zo rhyme with Me",
            "As far as rapping goes, you did your portion. We talk, play a game or I can tell your fortune",
            "As far as rhymes go, those were top shelf. But now why don't u tell me bout urself"
    };


    static String introAll ="";
    static String editorialAll ="";
    static String instructionsTwoAll ="";
    static String instructionsOneAll = "";
    static String outroAll ="";
    static String outroRapAgainAll ="";

    private StringUtil() {
    }

    private static volatile StringUtil sInstance;

    public static StringUtil INSTANCE() {

        if  (sInstance == null) {
            synchronized (StringUtil.class) {
                if (sInstance == null) {
                    sInstance = new StringUtil();
                    initializeJoinedStrings();
                }
            }
        }

        return sInstance;
    }

    ResponseStates findReplyResponseState(String str) {
        String checkString = str.toLowerCase().trim();
        Log.d("REPLY", "intro: "+introAll);
        if (introAll.contains(checkString)) {
            return ResponseStates.INTRO;
        }
        else if (editorialAll.contains(str.toLowerCase().trim())) {
            return ResponseStates.EDITORIAL;
        }
        else if (instructionsTwoAll.contains(str.toLowerCase().trim())) {
            return ResponseStates.INSTRUCTIONAL;
        }
        else if (instructionsOneAll.contains(str.toLowerCase().trim())) {
            return ResponseStates.INSTRUCTIONAL;
        }
        else if (outroAll.contains(str.toLowerCase().trim())) {
            return ResponseStates.OUTRO;
        }
        else if (outroRapAgainAll.contains(str.toLowerCase().trim())) {
            return ResponseStates.RAP_AGAIN;
        }
        return ResponseStates.OTHER;
    }

    int findReplyResponseInt(String str) {
        ResponseStates responseStates = findReplyResponseState(str);
        switch (responseStates) {
            case INTRO:
                return 0;
            case EDITORIAL:
                return 2;
            case INSTRUCTIONAL:
                return 3;
            case RAP_AGAIN:
                return 4;
            case OUTRO:
                return 1;
        }
        return 5;
    }

    static void initializeJoinedStrings() {
        introAll = buildStringFromArray(intro);
        outroAll = buildStringFromArray(outro);
        editorialAll = buildStringFromArray(editorial);
        instructionsOneAll = buildStringFromArray(furtherInstructionsLevelOne);
        instructionsTwoAll = buildStringFromArray(furtherInstructionsLevelTwo);
        outroRapAgainAll = buildStringFromArray(outroRapAgain);
    }

    static String buildStringFromArray(String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        for(String s : stringArray) {
            builder.append(s);
            builder.append(" ");
        }
        String str = builder.toString().toLowerCase();
        Log.d("buildStringFromArray", str);
        return str;
    }
}
