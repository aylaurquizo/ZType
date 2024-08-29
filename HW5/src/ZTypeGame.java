import tester.*;
import java.util.Random;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

//represents the constants in the code
interface WorldConstants {
  int SCREEN_HEIGHT = 600;
  int SCREEN_WIDTH = 400;
  int numBullets = 1;
  WorldImage spaceShip =  new BesideImage(
      new TriangleImage(new Posn(18, 0), new Posn(0, 24), new Posn(18, 24),
          OutlineMode.SOLID, Color.RED),
      new AboveImage(new EquilateralTriangleImage(10, OutlineMode.SOLID, Color.RED),
          new RectangleImage(10, 40, OutlineMode.SOLID, Color.GRAY)),
      new TriangleImage(new Posn(0, 0), new Posn(0, 24), new Posn(18, 24),
      OutlineMode.SOLID, Color.RED));

}

//represents a world class to animate a list of words on a scene
class ZTypeWorld extends World implements WorldConstants {
  ILoWord words;
  Random rand;
  int ticks;
  int score;

  /*
   * Template:
   * Fields:
   * ... this.words ...                               -- ILoWord
   * ... this.rand ...                                -- Random
   * Methods:
   *  ... this.words.makeScene() ...                  -- WorldScene
   * Methods for Fields:
   * ... this.words.endOfGame() ...                   -- boolean
   * ... this.words.anyEmpties() ...                  -- boolean
   * ... this.words.removeLetterHelp(IWord)           -- ILoWord
   * ... this.words.removeLetter(String)              -- ILoWord
   * ... this.words.remmoveFirstActive(String)        -- boolean
   * ... this.words.removeLetterActive(String)        -- ILoWord
   * ... this.words.anyActive() ...                   -- boolean
   * ... this.words.sort() ...                        -- ILoWord
   * ... this.words.sortInsert(IWord)...              -- ILoWord
   * ... this.words.isSorted()...                     -- boolean
   * ... this.words.isSortedHelper(IWord)...          -- boolean
   * ... this.words.interleave(ILoWord)...            -- ILoWord
   * ... this.words.merge(ILoWord)...                 -- ILoWord
   * ... this.words.checkAndReduce(String) ...        -- ILoWord
   * ... this.words.checkAndReduceHelper(String) ...  -- ILoWord
   * ... this.words.addToEnd(IWord)...                -- ILoWord
   * ... this.words.filterOutEmpties()...             -- ILoWord
   * ... this.words.draw(WorldScene) ...              -- WorldScene
   * ... this.move() ...                              -- ILoWord
   * ... this.selectWord(String) ...                 -- boolean
   */

  ZTypeWorld(ILoWord words, Random rand, int ticks, int score) {
    this.words = words;
    this.rand = rand;
    this.ticks = ticks;
    this.score = score;

  }

  ZTypeWorld(ILoWord words) {
    this(words, new Random(), 0, 0);
  }

  // draws the words onto the background
  public WorldScene makeScene() {
    return this.words.draw(
        new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT).placeImageXY(spaceShip, SCREEN_WIDTH / 2, 
            SCREEN_HEIGHT));
  }

  //draws the final scene of the world displaying "Game Over" and "Score:"
  public WorldScene makeAFinalScene() {
    return new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT).placeImageXY(
        new TextImage("Game Over", 20, Color.RED), 
        SCREEN_WIDTH / 2 , SCREEN_HEIGHT / 2).placeImageXY(
            new TextImage("Score:" + this.score, 20, Color.RED), SCREEN_WIDTH / 2 , 
            (SCREEN_HEIGHT / 2) + 30);
  }

  //move the words on the scene (adds a new Dot at a random location at every tick of the clock)
  public World onTick() {
    ILoWord add  = this.words.addToEnd(
        new InactiveWord(
            new Random().nextInt(SCREEN_WIDTH - 60) + 30, 0));
    if (this.ticks % 20 == 0) {
      return new ZTypeWorld(add.move(), this.rand, this.ticks + 1, this.score);
    }
    else {
      return new ZTypeWorld(this.words.move(), this.rand, this.ticks + 1, this.score);
    }    
  }
  
  //move the words on the scene (adds a new Dot at a random location at every tick of the clock)
  // for testing
  public World onTickForTesting() {
    ILoWord add  = this.words.addToEnd(
        new InactiveWord(
            new Utils().stringMaker(
                new Random(20), 0), 
            new Random(20).nextInt(SCREEN_WIDTH - 60) + 30, 0));
    if (this.ticks % 20 == 0) {
      return new ZTypeWorld(add.move(), this.rand, this.ticks + 1, this.score);
    }
    else {
      return new ZTypeWorld(this.words.move(), this.rand, this.ticks + 1, this.score);
    }    
  }
  
  

  // add a key event to remove one of the letters of an active word in this
  // World to a new string without the letter corresponding to the key pressed
  public World onKeyEvent(String key) {
    if (this.words.anyActive() && this.words.anyEmpties()) {
      return new ZTypeWorld(this.words.filterOutEmpties().removeLetter(key), this.rand, this.ticks,
          this.score + 1);
    }
    else if (this.words.selectWord(key) && !this.words.anyActive()) {
      return new ZTypeWorld(this.words.removeLetter(key), this.rand, this.ticks, this.score + 1);
    }
    else if (this.words.anyActive() && this.words.removeFirstActive(key)) {
      return new ZTypeWorld(this.words.removeLetterActive(key), this.rand, this.ticks,
          this.score + 1);
    }
    else {
      return this;
    }
  }

  //if a word hits the bottom, it ends the game
  public WorldEnd worldEnds() {
    if (this.words.endOfGame()) {
      return new WorldEnd(true, this.makeAFinalScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

//represents a list of words
interface ILoWord {

  //if a word hits the bottom, return true for the game is done
  boolean endOfGame();

  //returns true if there are any empties in the list
  boolean anyEmpties();

  //adds a IWord to the end of this list
  ILoWord removeLetterHelp(IWord word);

  //removes the first letter of the word if it matches the given string
  ILoWord removeLetter(String str);

  //returns true if there is an active word and it starts with the given letter
  boolean removeFirstActive(String str);

  //if a word is active and begins with the given letter, it removes the first letter
  ILoWord removeLetterActive(String str);

  //returns true if there are any active words in this list
  boolean anyActive();

  // sorts the list into alphabetical order
  ILoWord sort();

  // returns list with given word put in the front
  ILoWord sortInsert(IWord word);

  // determines if the list has been sorted into alphabetical order or not
  boolean isSorted();

  // helper for isSorted()
  boolean isSortedHelper(IWord word);

  // Produces a new list where the odd elements are from this list,
  // the even elements are from the given list, and the remaining
  // elements are put at the end of the list
  ILoWord interleave(ILoWord listOfIWords);

  // produces a sorted list by merging this sorted list and a given sorted list
  ILoWord merge(ILoWord listOfWords);

  // Produces a list of words where any word starting with the given string
  // is reduced by removing that first letter
  ILoWord checkAndReduce(String str);

  // helper for checkAndReduce
  ILoWord checkAndReduceHelper(String str);

  // produces a list of words that adds the given word to the end
  ILoWord addToEnd(IWord word);

  // produces a list of words that filters out any words that have an empty string
  ILoWord filterOutEmpties();

  // produces a world scene that draws all the words in the list onto the given
  // world scene.
  WorldScene draw(WorldScene world);

  // moves the list of words
  ILoWord move();

  //returns true if a word in this list starts with the given letter
  boolean selectWord(String str);

}

//represents an empty list of words
class MtLoWord implements ILoWord {

  /*
   * Template:
   * Fields:
   * 
   * Methods:
   * ... this.endOfGame() ...                         -- boolean
   * ... this.anyEmpties() ...                        -- boolean
   * ... this.removeLetterHelp(IWord)                 -- ILoWord
   * ... this.removeLetter(String)                    -- ILoWord
   * ... this.remmoveFirstActive(String)              -- boolean
   * ... this.removeLetterActive(String)              -- ILoWord
   * ... this.anyActive() ...                         -- boolean
   * ... this.sort() ...                              -- ILoWord
   * ... this.sortInsert(IWord)...                    -- ILoWord
   * ... this.isSorted()...                           -- boolean
   * ... this.isSortedHelper(IWord)...                -- boolean
   * ... this.interleave(ILoWord)...                  -- ILoWord
   * ... this.merge(ILoWord)...                       -- ILoWord
   * ... this.checkAndReduce(String) ...              -- ILoWord
   * ... this.checkAndReduceHelper(String) ...        -- ILoWord
   * ... this.addToEnd(IWord)...                      -- ILoWord
   * ... this.filterOutEmpties()...                   -- ILoWord
   * ... this.draw(WorldScene) ...                    -- WorldScene
   * ... this.move() ...                              -- ILoWord
   * ... this.selectWord(String) ...                  -- boolean
   */

  // sorts this list into alphabetical order
  public ILoWord sort() {
    return this;
  }

  // helper for sort which returns partially sorted list
  public ILoWord sortInsert(IWord first) {
    return new ConsLoWord(first, new MtLoWord());
  }

  // determines if this list has been sorted into alphabetical order or not
  public boolean isSorted() {
    return true;
  }

  // helper for isSorted()
  public boolean isSortedHelper(IWord word) {
    return true;
  }

  // Produces a new list where the odd elements are from this list,
  // the even elements are from the given list, and the remaining
  // elements are put at the end of the list
  public ILoWord interleave(ILoWord listOfIWords) {
    return listOfIWords;
  }

  // produces a sorted list by merging this sorted list and a the given sorted
  // list
  public ILoWord merge(ILoWord listOfWords) {
    return listOfWords;
  }

  // Produces a list of words where any word starting with the given string
  // is reduced by removing that first letter
  public ILoWord checkAndReduce(String str) {
    return this;
  }

  // helper for checkAndReduce
  public ILoWord checkAndReduceHelper(String str) {
    return this;
  }

  // produces a list of words that adds the given word to the end
  public ILoWord addToEnd(IWord word) {
    return new ConsLoWord(word, this);
  }

  // produces a list of words that filters out any words that have an empty string
  public ILoWord filterOutEmpties() {
    return this;
  }

  // produces a world scene that draws all the words in the list onto the given
  // world scene.
  public WorldScene draw(WorldScene world) {
    return world;
  }

  // move the words in this empty list
  public ILoWord move() {
    return this;
  }

  //returns true if a word in this list starts with the given letter
  public boolean selectWord(String str) {
    return false;
  }

  //returns true if there are any active words in this list
  public boolean anyActive() {
    return false;
  }

  //returns true if there is an active word and it starts with the given letter
  public boolean removeFirstActive(String str) {
    return false;
  }

  //removes the first letter of the word if it matches the given string
  public ILoWord removeLetter(String str) {
    return this;
  }

  //adds a IWord to the end of this list
  public ILoWord removeLetterHelp(IWord word) {
    return this;
  }

  //if a word is active and begins with the given letter, it removes the first letter
  public ILoWord removeLetterActive(String str) {
    return this;
  }

  //returns true if there are any empties in this list
  public boolean anyEmpties() {
    return false;
  }

  //returns true if any of the words in this list are at the bottom
  public boolean endOfGame() {
    return false;
  }

}

//class to represent a list of words
class ConsLoWord implements ILoWord {
  /*
   * Template:
   * Fields:
   * ... this.first ...                               -- IWord
   * ... this.rest ...                                -- ILoWord
   * Methods:
   * ... this.endOfGame() ...                         -- boolean
   * ... this.anyEmpties() ...                        -- boolean
   * ... this.removeLetterHelp(IWord)                 -- ILoWord
   * ... this.removeLetter(String)                    -- ILoWord
   * ... this.remmoveFirstActive(String)              -- boolean
   * ... this.removeLetterActive(String)              -- ILoWord
   * ... this.anyActive() ...                         -- boolean
   * ... this.sort() ...                              -- ILoWord
   * ... this.sortInsert(IWord)...                    -- ILoWord
   * ... this.isSorted()...                           -- boolean
   * ... this.isSortedHelper(IWord)...                -- boolean
   * ... this.interleave(ILoWord)...                  -- ILoWord
   * ... this.merge(ILoWord)...                       -- ILoWord
   * ... this.checkAndReduce(String) ...              -- ILoWord
   * ... this.checkAndReduceHelper(String) ...        -- ILoWord
   * ... this.addToEnd(IWord)...                      -- ILoWord
   * ... this.filterOutEmpties()...                   -- ILoWord
   * ... this.draw(WorldScene) ...                    -- WorldScene
   * ... this.move() ...                              -- ILoWord
   * ... this.selectWord(String) ...                  -- boolean
   * Methods for Fields:
   * ... this.first.atBottom() ...                    -- boolean
   * ... this.first.removeLetterHelper() ...          -- IWord
   * ... this.first.matchesStr(String) ...            -- boolean
   * ... this.first.anyActiveHelper() ...             -- boolean
   * ... this.first.sort(IWord) ...                   -- int
   * ... this.first.sortHelper(String) ...            -- int
   * ... this.first.isEmpty() ...                     -- boolean
   * ... this.first.checkAndReduce(String) ...        -- IWord
   * ... this.first.draw(WorldScene) ...              -- WorldScene
   * ... this.first.move() ...                        -- IWord
   * ... this.rest.endOfGame() ...                    -- boolean
   * ... this.rest.anyEmpties() ...                   -- boolean
   * ... this.rest.removeLetterHelp(IWord)            -- ILoWord
   * ... this.rest.removeLetter(String)               -- ILoWord
   * ... this.rest.remmoveFirstActive(String)         -- boolean
   * ... this.rest.removeLetterActive(String)         -- ILoWord
   * ... this.rest.anyActive() ...                    -- boolean
   * ... this.rest.sort() ...                         -- ILoWord
   * ... this.rest.sortInsert(IWord)...               -- ILoWord
   * ... this.rest.isSorted()...                      -- boolean
   * ... this.rest.isSortedHelper(IWord)...           -- boolean
   * ... this.rest.interleave(ILoWord)...             -- ILoWord
   * ... this.rest.merge(ILoWord)...                  -- ILoWord
   * ... this.rest.checkAndReduce(String) ...         -- ILoWord
   * ... this.rest.checkAndReduceHelper(String) ...   -- ILoWord
   * ... this.rest.addToEnd(IWord)...                 -- ILoWord
   * ... this.rest.filterOutEmpties()...              -- ILoWord
   * ... this.rest.draw(WorldScene) ...               -- WorldScene
   * ... this.rest.move() ...                         -- ILoWord
   * ... this.rest.selectWord(String) ...             -- boolean
   */

  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  // sorts this list into alphabetical order
  public ILoWord sort() {
    return this.rest.sort().sortInsert(this.first);
  }

  // helper for sort which returns a partially sorted list
  public ILoWord sortInsert(IWord word) {
    if (this.first.sort(word) < 0) {
      return new ConsLoWord(word, this);
    }
    else {
      return new ConsLoWord(this.first, this.rest.sortInsert(word));
    }
  }

  // determines if this list has been sorted into alphabetical order or not
  public boolean isSorted() {
    return this.rest.isSortedHelper(this.first) && this.rest.isSorted();
  }

  // helper for isSorted
  public boolean isSortedHelper(IWord word) {
    return this.first.sort(word) <= 0;
  }

  // Produces a new list where the odd elements are from this list,
  // the even elements are from the given list, and the remaining
  // elements are put at the end of the list
  public ILoWord interleave(ILoWord listOfIWords) {
    return new ConsLoWord(this.first, listOfIWords.interleave(this.rest));
  }

  // returns a sorted list of two combined sorted lists treating elements as all
  // lowercase
  public ILoWord merge(ILoWord listOfWords) {
    return this.rest.merge(listOfWords.sortInsert(this.first));
  }

  // Produces a list of words where any word starting with the given string
  // is reduced by removing that first letter
  public ILoWord checkAndReduce(String str) {
    return new ConsLoWord(this.first.checkAndReduce(str), this.rest.checkAndReduceHelper(str));
  }

  // helper for checkAndReduce
  public ILoWord checkAndReduceHelper(String str) {
    return checkAndReduce(str);
  }

  // produces a list of words that adds the given word to the end
  public ILoWord addToEnd(IWord word) {
    return new ConsLoWord(this.first, this.rest.addToEnd(word));
  }

  // produces a list of words that filters out any words that have an empty string
  public ILoWord filterOutEmpties() {
    if (this.first.isEmpty()) {
      return this.rest.filterOutEmpties();
    }
    else {
      return new ConsLoWord(this.first, this.rest.filterOutEmpties());
    }
  }

  // produces a world scene that draws all the words in the list onto the given
  // world scene.
  public WorldScene draw(WorldScene scene) {
    return this.rest.draw(this.first.draw(scene));
  }

  // move the words in this non-empty list
  public ILoWord move() {
    return new ConsLoWord(this.first.move(), this.rest.move());
  }

  //returns true if a word in this list starts with the given letter
  public boolean selectWord(String str) {
    if (this.first.matchesStr(str)) {
      return true;
    }
    else {
      return this.rest.selectWord(str);
    }
  }

  //returns true if there are any active words in this list
  public boolean anyActive() {
    return this.first.anyActiveHelper() || this.rest.anyActive();
  }

  //returns true if there is an active word and it starts with the given letter
  public boolean removeFirstActive(String str) {
    if (this.first.anyActiveHelper() && this.first.matchesStr(str)) {
      return true;
    }
    else {
      return this.rest.removeFirstActive(str);
    }
  }

  //removes the first letter of the word if it matches the given string
  public ILoWord removeLetter(String str) {
    if (this.first.matchesStr(str)) {
      return new ConsLoWord(this.first.removeLetterHelper(), this.rest);
    }
    else {
      return this.rest.removeLetterHelp(this.first).removeLetter(str);
    }
  }

  //adds a IWord to the end of this list
  public ILoWord removeLetterHelp(IWord word) {
    return new ConsLoWord(this.first, this.rest.addToEnd(word));
  }

  //if a word is active and begins with the given letter, it removes the first letter
  public ILoWord removeLetterActive(String str) {
    if (this.first.anyActiveHelper() && this.first.matchesStr(str)) {
      return new ConsLoWord(this.first.removeLetterHelper(), this.rest);
    }
    else {
      return this.rest.removeLetterHelp(this.first).removeLetterActive(str);
    }
  }

  //returns true if there are any empties in the list
  public boolean anyEmpties() {
    if (this.first.isEmpty()) {
      return true;
    }
    else {
      return this.rest.anyEmpties();
    }
  }

  //returns true if any words in this list are at the bottom
  public boolean endOfGame() {
    if (this.first.atBottom()) {
      return true;
    }
    else {
      return this.rest.endOfGame();
    }
  }
}

//represents a word in the ZType game
interface IWord {

  //returns true if this word is at the bottom of the screen
  boolean atBottom();

  //removes the first letter of this word and makes it active if not already
  IWord removeLetterHelper();

  //returns true if this word starts with the given letter
  boolean matchesStr(String str);

  //returns true if this word is active
  boolean anyActiveHelper();

  // helper for ILoWord sort method
  int sort(IWord word);

  // compares the two words to see which comes first alphabetically
  int sortHelper(String str);

  // determines if a word is empty
  boolean isEmpty();

  // checks to see if a word starts with the given string and if it does
  // reduces the word to not include the first letter
  IWord checkAndReduce(String str);

  // draws the word
  WorldScene draw(WorldScene scene);

  //moves the word down and towards the ship
  public abstract IWord move();
}

abstract class AWord implements IWord, WorldConstants {
  /*
   * Template:
   * Fields:
   * ... this.word ...                                -- String
   * ... this.x ...                                   -- int
   * ... this.y ...                                   -- int
   * Methods:
   * ... this.atBottom() ...                          -- boolean
   * ... this.removeLetterHelper() ...                -- IWord
   * ... this.matchesStr(String) ...                  -- boolean
   * ... this.anyActiveHelper() ...                   -- boolean
   * ... this.sort(IWord) ...                         -- int
   * ... this.sortHelper(String) ...                  -- int
   * ... this.isEmpty() ...                           -- boolean
   * ... this.checkAndReduce(String) ...              -- IWord
   * ... this.draw(WorldScene) ...                    -- WorldScene
   * ... this.move() ...                              -- IWord
   * Methods for Fields:
   * 
   */

  String word;
  int x;
  int y;

  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  //returns true if this word is at the bottom of the screen
  public boolean atBottom() {
    return this.y == SCREEN_HEIGHT - 30;
  }

  // helper for ILoWord sort method
  public int sort(IWord word) {
    return word.sortHelper(this.word);
  }

  // compares the two words to see which comes first alphabetically
  public int sortHelper(String str) {
    return this.word.toLowerCase().compareTo(str.toLowerCase());
  }

  // determines if word is empty, filter out empties helper
  public boolean isEmpty() {
    return this.word.isEmpty();
  }

  // does not reduce inactive word
  public IWord checkAndReduce(String ch) {
    return this;
  }

  // draws the word
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.word, 20, FontStyle.REGULAR, Color.BLUE), this.x,
        this.y);
  }

  //moves this word down and towards the ship
  public abstract IWord move();

  //returns true if this word is active
  public boolean anyActiveHelper() {
    return false;
  }

}

//represents an active word in the ZType game
class ActiveWord extends AWord {

  /*
   * Template is everything in the template for the abstract class AWord
   */

  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  ActiveWord(int x, int y) {
    this(new Utils().stringMaker(new Random(), 0), x, y);
  }

  // checks to see if a word starts with the given string and if it does
  // reduces the word to not include the first letter
  public IWord checkAndReduce(String str) {
    if (!this.word.isEmpty() && this.word.substring(0, 1).equalsIgnoreCase(str)) {
      return new ActiveWord(this.word.substring(1), this.x, this.y);
    }
    return this;
  }

  // draws the word
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.word, 20, FontStyle.REGULAR, Color.RED), this.x,
        this.y);
  }

  //moves the position of the word down and towards the ship
  public IWord move() {
    int dx = SCREEN_WIDTH / 2 - this.x;
    int dy = SCREEN_HEIGHT - this.y;

    double magnitude = Math.sqrt(dx * dx + dy * dy);

    int unitX = (int) Math.round(dx / magnitude);

    int newX = this.x + unitX * 3;

    return new ActiveWord(this.word, newX, this.y + 1);
  }

  //returns true is this word is active
  @Override
  public boolean anyActiveHelper() {
    return true;
  }

  //returns true if this word starts with the given letter
  public boolean matchesStr(String str) {
    return this.word.substring(0, 1).equals(str);
  }

  //removes the first letter of this word
  public IWord removeLetterHelper() {
    return new ActiveWord(this.word.substring(1), this.x, this.y);
  }
}

//represents an inactive word in the ZType game
class InactiveWord extends AWord {

  /*
   * Template is everything in the template for the abstract class AWord
   */

  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  InactiveWord(int x, int y) {
    this(new Utils().stringMaker(new Random(), 0), x, y);
  }

  //moves this word down and towards the ship
  @Override
  public IWord move() {
    
    int dx = SCREEN_WIDTH / 2 - this.x;
    int dy = SCREEN_HEIGHT  - this.y;

    double magnitude = Math.sqrt(dx * dx + dy * dy);

    // Calculate the unit vector
    int unitX = (int) Math.round(dx / magnitude);

    int newX = this.x + unitX * 3;

    return new InactiveWord(this.word, newX, this.y + 1);
    //return new InactiveWord(this.word, x, this.y + 1);
  }

  //returns true if this word starts with the given letter
  public boolean matchesStr(String str) {
    return this.word.substring(0, 1).equals(str);
  }

  //removes the first letter of this word and makes it active
  public IWord removeLetterHelper() {
    return new ActiveWord(this.word.substring(1), this.x, this.y);
  }

}

class Utils {
  
  /*
   * Template:
   * Fields:
   * Methods:
   *  ... this.stringMaker(Random, int) ...                  -- String
   *  ...
   */

  String alphabet = "abcdefghijklmnopqrstuvwxyz";

  //creates a random word using the letters of the alphabet
  String stringMaker(Random rand, int acc) {
    if (acc < 6) {
      return alphabet.charAt(Math.abs(rand.nextInt()) % 26)
          + new Utils().stringMaker(rand, acc + 1);
    }
    else {
      return "";
    }
  }
}

//all examples and tests for ILoWord
class ExamplesZTypeAnimation {

  IWord red = new ActiveWord("red", 10, 45);
  IWord orange = new ActiveWord("orange", 20, 10);
  IWord yellow = new ActiveWord("yellow", 30, 10);
  IWord green = new ActiveWord("green", 40, 10);
  IWord blue = new ActiveWord("blue", 40, 30);
  IWord purple = new ActiveWord("purple", 60, 10);
  IWord pink = new ActiveWord("pink", 70, 10);
  IWord white = new ActiveWord("white", 30, 570);

  IWord cat = new ActiveWord("cat", 10, 20);
  IWord dog = new ActiveWord("dog", 10, 30);
  IWord mouse = new ActiveWord("mouse", 10, 40);
  IWord turtle = new ActiveWord("turtle", 10, 50);
  IWord snake = new InactiveWord("snake", 10, 60);
  IWord donkey = new InactiveWord("donkey", 10, 70);
  IWord horse = new InactiveWord("horse", 300, 570);

  IWord HELLO = new ActiveWord("HELLO", 1, 1);
  IWord Goodbye = new ActiveWord("Goodbye", 1, 2);

  IWord empty1 = new ActiveWord("", 10, 10);
  IWord empty2 = new InactiveWord("", 30, 30);

  ILoWord mt = new MtLoWord();

  ILoWord list1 = new ConsLoWord(this.red, this.mt);
  ILoWord list2 = new ConsLoWord(this.orange, this.list1);
  ILoWord list3 = new ConsLoWord(this.yellow, this.list2);
  ILoWord list4 = new ConsLoWord(this.green, this.list3);
  ILoWord list5 = new ConsLoWord(this.purple, this.list4);
  ILoWord list6 = new ConsLoWord(this.pink, this.list5);
  ILoWord listAtBottom1 = new ConsLoWord(this.white, this.list6);
  ILoWord listAtBottom2 = new ConsLoWord(this.horse, this.list5);

  ILoWord animals0 = new ConsLoWord(this.donkey, new MtLoWord());
  ILoWord animals1 = new ConsLoWord(this.cat, new MtLoWord());
  ILoWord animals2 = new ConsLoWord(this.dog, this.animals1);
  ILoWord animals3 = new ConsLoWord(this.mouse, this.animals2);
  ILoWord animals4 = new ConsLoWord(this.turtle, this.animals3);
  ILoWord animals5 = new ConsLoWord(this.snake, this.animals4);
  ILoWord animals6 = new ConsLoWord(this.donkey, this.animals5);
  ILoWord animals7 = new ConsLoWord(this.snake, this.animals2);
  ILoWord animals8 = new ConsLoWord(this.snake, this.animals0);

  ILoWord list7 = new ConsLoWord(this.empty1, this.list1);
  ILoWord list8 = new ConsLoWord(this.empty2, this.list7);

  ILoWord list9 = new ConsLoWord(this.blue, new MtLoWord());

  ILoWord list10 = new ConsLoWord(this.green, this.list1);
  ILoWord list11 = new ConsLoWord(this.blue, this.list10);

  ILoWord list12 = new ConsLoWord(this.donkey, this.mt);
  ILoWord list13 = new ConsLoWord(this.dog, this.list12);
  ILoWord list14 = new ConsLoWord(this.cat, this.list13);

  ILoWord list15 = new ConsLoWord(this.mouse, this.list14);
  ILoWord list16 = new ConsLoWord(this.donkey, this.list15);

  ILoWord list17 = new ConsLoWord(this.blue, this.list1);

  ILoWord list18 = new ConsLoWord(this.Goodbye, this.list1);
  ILoWord list19 = new ConsLoWord(this.HELLO, this.list18);

  // examples for scene
  WorldImage redImage = new TextImage("red", 20, Color.RED);
  WorldImage blueImage = new TextImage("blue", 20, Color.RED);
  WorldImage pinkImage = new TextImage("pink", 20, Color.RED);
  WorldImage mtImage = new EmptyImage();
  WorldImage catImage = new TextImage("cat", 20, Color.RED);
  WorldImage dogImage = new TextImage("dog", 20, Color.RED);
  WorldImage snakeImage = new TextImage("snake", 20, Color.BLUE);
  WorldImage donkeyImage = new TextImage("donkey", 20, Color.BLUE);

  ILoWord redList = new ConsLoWord(this.red, this.mt);
  ILoWord blueList = new ConsLoWord(this.blue, this.redList);

  WorldScene scene1 = new WorldScene(100, 100);
  WorldScene scene2 = scene1.placeImageXY(this.redImage, 10, 45);
  WorldScene scene3 = scene1.placeImageXY(this.blueImage, 40, 30).placeImageXY(this.redImage, 10,
      45);

  // examples for ZTypeWorld
  ZTypeWorld world5 = new ZTypeWorld(this.mt, new Random(2), 15, 0);
  ZTypeWorld world6 = new ZTypeWorld(this.list2, new Random(2), 14, 5);
  ZTypeWorld world7 = new ZTypeWorld(this.list2, new Random(2), 20, 5);
  ZTypeWorld world8 = new ZTypeWorld(this.animals7, new Random(2), 15, 4);
  ZTypeWorld world9 = new ZTypeWorld(this.animals8, new Random(2), 14, 5);
  
  // image of the space ship
  WorldImage spaceShip = new BesideImage(
      new TriangleImage(new Posn(18, 0), new Posn(0, 24), new Posn(18, 24),
          OutlineMode.SOLID, Color.RED),
      new AboveImage(new EquilateralTriangleImage(10, OutlineMode.SOLID, Color.RED),
          new RectangleImage(10, 40, OutlineMode.SOLID, Color.GRAY)),
      new TriangleImage(new Posn(0, 0), new Posn(0, 24), new Posn(18, 24),
      OutlineMode.SOLID, Color.RED));
  
  // example for starting WorldScene
  WorldScene startingScene = new WorldScene(400, 600).placeImageXY(spaceShip, 400 / 2, 
      600);
  
  
  //testing makeScene() for a ZTypeWorld
  boolean testMakeScene(Tester t) {
    return
    // test for a ZTypeWorld with no words displayed
    t.checkExpect(this.world5.makeScene(), this.startingScene)
        // test for a ZTypeWorld with both inactive and active words
        && t.checkExpect(this.world8.makeScene(),
            this.startingScene.placeImageXY(this.snakeImage, 10, 60)
                .placeImageXY(this.dogImage, 10, 30).placeImageXY(catImage, 10, 20))
        // test for a ZTypeWolrd with only inactive words
        && t.checkExpect(this.world9.makeScene(), this.startingScene
            .placeImageXY(this.snakeImage, 10, 60).placeImageXY(this.donkeyImage, 10, 70));

  }

  //test for sort
  boolean testSort(Tester t) {
    return
    // tests an empty list of word
    t.checkExpect(this.mt.sort(), this.mt)
        // test for a list with 1 word
        && t.checkExpect(this.list1.sort(), this.list1)
        // test for a list with 2 words
        && t.checkExpect(this.list2.sort(), this.list2)
        // test for a list with 3 words
        && t.checkExpect(this.list3.sort(), new ConsLoWord(this.orange,
            new ConsLoWord(this.red, new ConsLoWord(this.yellow, new MtLoWord()))))
        // test for a list with 4 words
        && t.checkExpect(this.list4.sort(), new ConsLoWord(this.green, new ConsLoWord(this.orange,
            new ConsLoWord(this.red, new ConsLoWord(this.yellow, new MtLoWord())))))
        // test for a list with 5 words
        && t.checkExpect(this.list5.sort(),
            new ConsLoWord(this.green, new ConsLoWord(this.orange, new ConsLoWord(this.purple,
                new ConsLoWord(this.red, new ConsLoWord(this.yellow, new MtLoWord()))))))
        // test for a list with 6 words
        && t.checkExpect(this.list6.sort(),
            new ConsLoWord(this.green,
                new ConsLoWord(this.orange, new ConsLoWord(this.pink, new ConsLoWord(this.purple,
                    new ConsLoWord(this.red, new ConsLoWord(this.yellow, new MtLoWord())))))))
        // test for a list of words with upper and lower case strings
        && t.checkExpect(this.list19.sort(), new ConsLoWord(this.Goodbye,
            new ConsLoWord(this.HELLO, new ConsLoWord(this.red, this.mt))));
  }

  //test for isSorted
  boolean testIsSorted(Tester t) {
    return
    // test for empty list
    t.checkExpect(this.mt.isSorted(), true)
        // test with 1 word
        && t.checkExpect(this.list1.isSorted(), true)
        // test with 2 words
        && t.checkExpect(this.list2.isSorted(), true)
        // test with 3 words
        && t.checkExpect(this.list3.isSorted(), false)
        // test with 6 words
        && t.checkExpect(this.animals6.isSorted(), false)
        // test with 5 words
        && t.checkExpect(this.list16.isSorted(), false)
        // tests for lists with upper and lower case strings
        && t.checkExpect(this.list18.isSorted(), true)
        // tests for lists with upper and lower case strings
        && t.checkExpect(this.list19.isSorted(), false);
  }

  //test for interleave
  boolean testInterleave(Tester t) {
    return
    // test that is invoked by an empty list and takes in an empty list
    t.checkExpect(this.mt.interleave(this.mt), this.mt)
        // test that is invoked by an empty list and takes in a non empty list
        && t.checkExpect(this.mt.interleave(this.list10), this.list10)
        // test that is invoked by a non empty list and takes in an empty list
        && t.checkExpect(this.list12.interleave(this.mt), this.list12)
        // test where given list is shorter than this list
        && t.checkExpect(this.animals4.interleave(this.list3),
            new ConsLoWord(this.turtle,
                new ConsLoWord(this.yellow,
                    new ConsLoWord(this.mouse, new ConsLoWord(this.orange, new ConsLoWord(this.dog,
                        new ConsLoWord(this.red, new ConsLoWord(this.cat, this.mt))))))))
        // test where given list is longer than this list
        && t.checkExpect(this.list3.interleave(this.animals4),
            new ConsLoWord(this.yellow,
                new ConsLoWord(this.turtle,
                    new ConsLoWord(this.orange, new ConsLoWord(this.mouse, new ConsLoWord(this.red,
                        new ConsLoWord(this.dog, new ConsLoWord(this.cat, this.mt))))))))
        // test with given list is longer than this list and repeat words
        && t.checkExpect(this.list1.interleave(this.list2), new ConsLoWord(this.red,
            new ConsLoWord(this.orange, new ConsLoWord(this.red, new MtLoWord()))))
        // test with given list is much shorter than this list
        && t.checkExpect(this.list6.interleave(this.animals3), new ConsLoWord(this.pink,
            new ConsLoWord(this.mouse, new ConsLoWord(this.purple, new ConsLoWord(this.dog,
                new ConsLoWord(this.green, new ConsLoWord(this.cat, new ConsLoWord(this.yellow,
                    new ConsLoWord(this.orange, new ConsLoWord(this.red, new MtLoWord()))))))))))
        // test with inactive words
        && t.checkExpect(this.list14.interleave(this.list11),
            new ConsLoWord(this.cat,
                new ConsLoWord(this.blue, new ConsLoWord(this.dog, new ConsLoWord(this.green,
                    new ConsLoWord(this.donkey, new ConsLoWord(this.red, new MtLoWord())))))));
  }

  //test for merge
  boolean testMerge(Tester t) {
    return
    // test that is invoked by an empty list and takes in an empty list
    t.checkExpect(this.mt.merge(this.mt), this.mt)
        // test that is invoked by an empty list and takes in a non empty sorted list
        && t.checkExpect(this.mt.merge(this.list3.sort()), this.list3.sort())
        // test that is invoked by a non empty sorted list and takes in an empty list
        && t.checkExpect(this.list14.sort().merge(this.mt), this.list14.sort())
        // test where given list is longer than this list
        && t.checkExpect(this.list1.sort().merge(animals3.sort()),
            new ConsLoWord(this.cat, new ConsLoWord(this.dog,
                new ConsLoWord(this.mouse, new ConsLoWord(this.red, new MtLoWord())))))
        // test where given list is shorter than this list
        && t.checkExpect(
            this.list6.sort().merge(animals4
                .sort()),
            new ConsLoWord(this.cat,
                new ConsLoWord(this.dog,
                    new ConsLoWord(this.green,
                        new ConsLoWord(this.mouse,
                            new ConsLoWord(this.orange,
                                new ConsLoWord(this.pink,
                                    new ConsLoWord(this.purple,
                                        new ConsLoWord(this.red, new ConsLoWord(this.turtle,
                                            new ConsLoWord(this.yellow, new MtLoWord())))))))))))
        // test where this list is longer than given
        && t.checkExpect(this.list6.sort().merge(this.animals3.sort()), new ConsLoWord(this.cat,
            new ConsLoWord(this.dog, new ConsLoWord(this.green, new ConsLoWord(this.mouse,
                new ConsLoWord(this.orange, new ConsLoWord(this.pink, new ConsLoWord(this.purple,
                    new ConsLoWord(this.red, new ConsLoWord(this.yellow, new MtLoWord()))))))))))
        // test with inactive words
        && t.checkExpect(this.list14.sort().merge(this.list11.sort()),
            new ConsLoWord(this.blue,
                new ConsLoWord(this.cat, new ConsLoWord(this.dog, new ConsLoWord(this.donkey,
                    new ConsLoWord(this.green, new ConsLoWord(this.red, this.mt)))))));

  }

  //test for checkAndReduce
  boolean testCheckAndReduce(Tester t) {
    return
    // test that is invoked by an empty list
    t.checkExpect(this.mt.checkAndReduce("i"), this.mt)
        // test with 1 reduced word
        && t.checkExpect(this.list3.checkAndReduce("o"),
            new ConsLoWord(this.yellow, new ConsLoWord(new ActiveWord("range", 20, 10),
                new ConsLoWord(this.red, new MtLoWord()))))
        // test with no reduced word
        && t.checkExpect(this.list2.checkAndReduce("e"), this.list2)
        // test with inactive word starting with character
        && t.checkExpect(this.animals5.checkAndReduce("s"), this.animals5)
        // test with inactive word and active word reduced
        && t.checkExpect(this.animals6.checkAndReduce("d"),
            new ConsLoWord(this.donkey,
                new ConsLoWord(this.snake,
                    new ConsLoWord(this.turtle,
                        new ConsLoWord(this.mouse, new ConsLoWord(new ActiveWord("og", 10, 30),
                            new ConsLoWord(this.cat, new MtLoWord())))))));
  }

  //test for addToEnd
  boolean testAddToEnd(Tester t) {
    return
    // test that is invoked by an empty list
    t.checkExpect(this.mt.addToEnd(this.red), this.list1)
        // test with add word to list
        && t.checkExpect(this.list1.addToEnd(this.blue),
            new ConsLoWord(this.red, new ConsLoWord(this.blue, new MtLoWord())))
        // test to add inactive word to list
        && t.checkExpect(this.animals3.addToEnd(this.donkey),
            new ConsLoWord(this.mouse, new ConsLoWord(this.dog,
                new ConsLoWord(this.cat, new ConsLoWord(this.donkey, new MtLoWord())))))
        // test to add same word to list
        && t.checkExpect(this.list3.addToEnd(this.red),
            new ConsLoWord(this.yellow, new ConsLoWord(this.orange,
                new ConsLoWord(this.red, new ConsLoWord(this.red, new MtLoWord())))));
  }

  //test for filterOutEmpties
  boolean testFilterOutEmpties(Tester t) {
    return
    // test that is invoked by a non empty list that has more than
    // one empty word
    t.checkExpect(this.list8.filterOutEmpties(), this.list1)
        // test invoked by a non empty list
        && t.checkExpect(this.list4.filterOutEmpties(), this.list4)
        // test that is invoked by an empty list
        && t.checkExpect(this.mt.filterOutEmpties(), this.mt);

  }
  
  //test for draw
  boolean testdraw(Tester t) {
    return
    // test invoked by an empty list
    t.checkExpect(this.mt.draw(this.scene1), this.scene1)
        // test invoked by a non empty list
        && t.checkExpect(this.redList.draw(this.scene1), this.scene2)
        // test with multiple words
        && t.checkExpect(this.blueList.draw(this.scene1), this.scene3);
  }

  //test for Utils
  boolean testUtils(Tester t) {
    return
    // test for stringMaker using different seeds
    t.checkExpect(new Utils().stringMaker(new Random(1), 0), "napsyw")
        && t.checkExpect(new Utils().stringMaker(new Random(2), 0), "lmwrin")
        && t.checkExpect(new Utils().stringMaker(new Random(3), 0), "myzigi")
        && t.checkExpect(new Utils().stringMaker(new Random(4), 0), "qmvdlf");
  }

  //test for isEmpty
  boolean testIsEmpty(Tester t) {
    return
    // test for a non-empty string active word
    t.checkExpect(this.red.isEmpty(), false)
        // test for a non-empty string inactive word
        && t.checkExpect(this.donkey.isEmpty(), false)
        // test for an empty string word
        && t.checkExpect(this.empty1.isEmpty(), true);
  }

  //test for sortInsert
  boolean testSortInsert(Tester t) {
    return
    // test empty
    t.checkExpect(this.mt.sortInsert(this.red), new ConsLoWord(this.red, this.mt))
        // test same words
        && t.checkExpect(this.list1.sortInsert(this.red), new ConsLoWord(this.red, this.list1))
        // test word inserted at beginning
        && t.checkExpect(this.list1.sortInsert(this.blue), new ConsLoWord(this.blue, this.list1))
        // test word put in middle
        && t.checkExpect(this.list4.sortInsert(this.purple),
            new ConsLoWord(this.green, new ConsLoWord(this.purple, new ConsLoWord(this.yellow,
                new ConsLoWord(this.orange, new ConsLoWord(this.red, this.mt))))));

  }

  //test for isSortedHelper
  boolean testIsSortedHelper(Tester t) {
    return 
        //tests empty case
        t.checkExpect(this.mt.isSortedHelper(this.red), true)
        //tests lists
        && t.checkExpect(this.list1.isSortedHelper(this.blue), true)
        && t.checkExpect(this.list5.isSortedHelper(this.cat), true)
        && t.checkExpect(this.animals3.isSortedHelper(this.blue), true);
  }

  //test for checkAndReduceHelper
  boolean testCheckAndReduceHelper(Tester t) {
    return 
        //tests empty case
        t.checkExpect(this.mt.checkAndReduceHelper("h"), this.mt)
        //tests list where no word is reduced
        && t.checkExpect(this.list1.checkAndReduceHelper("b"), this.list1)
        //tests list where word is reduced
        && t.checkExpect(this.list1.checkAndReduceHelper("r"),
            new ConsLoWord(new ActiveWord("ed", 10, 45), this.mt));
  }

  //test for sortHelper
  boolean testSortHelper(Tester t) {
    return 
        //tests given word comes before this word
        t.checkExpect(this.red.sortHelper("blue"), 16)
        //tests give word is equal to this word
        && t.checkExpect(this.red.sortHelper("red"), 0)
        //tests given word comes after this word
        && t.checkExpect(this.red.sortHelper("yellow"), -7);
  }

  // test for the move method in this ILoWord
  boolean testILoWordMove(Tester t) {
    // testing on an empty list
    return t.checkExpect(this.mt.move(), this.mt)
        // testing for an ILoWord with active and inactive words
        && t.checkExpect(this.animals7.move(),
            new ConsLoWord(new InactiveWord("snake", 10, 60 + 1),
                new ConsLoWord(new ActiveWord("dog", 10, 30 + 1),
                    new ConsLoWord(new ActiveWord("cat", 10, 20 + 1), this.mt))))
        // testing for ILoWord with only active words
        && t.checkExpect(this.list2.move(), new ConsLoWord(new ActiveWord("orange", 20, 10 + 1),
            new ConsLoWord(new ActiveWord("red", 10, 45 + 1), this.mt)));
  }

  // test for the move method in an IWord
  boolean testIWord(Tester t) {
    // testing move on an ActiveWord
    return t.checkExpect(this.red.move(), new ActiveWord("red", 10, 45 + 1))
        && t.checkExpect(this.orange.move(), new ActiveWord("orange", 20, 10 + 1))
        // testing move on an InactiveWord
        && t.checkExpect(this.snake.move(), new InactiveWord("snake", 10, 60 + 1))
        && t.checkExpect(this.donkey.move(), new InactiveWord("donkey", 10, 70 + 1));
  }

  // test for removeFirstActive
  boolean testRemoveFirstActive(Tester t) {
    // testing removeFirstActive on an empty list
    return t.checkExpect(this.mt.removeFirstActive(""), false)
        // testing on an ILoWord with only inactive words with a given String
        // that is empty
        && t.checkExpect(this.animals8.removeFirstActive(""), false)
        // testing on ILoWord with only inactive words with a given String that
        // matches the first letter of one of the words in ILoWord
        && t.checkExpect(this.animals8.removeFirstActive("s"), false)
        // testing on an ILoWord with only active words with a given String that
        // is empty
        && t.checkExpect(this.list3.removeFirstActive(""), false)
        // testing on an ILoWord with only active words with a given String that
        // matches the first letter of one of the words in this list
        && t.checkExpect(this.list3.removeFirstActive("r"), true);
  }

  // test for removeLetterActive
  boolean testRemoveLetteActive(Tester t) {
    // testing removeLetterActive on an empty list
    return t.checkExpect(this.mt.removeLetterActive(""), this.mt)
        // testing on an ILoWord where the first word is active and
        // it start with the given String
        && t.checkExpect(this.list3.removeLetterActive("y"),
            new ConsLoWord(new ActiveWord("ellow", 30, 10), this.list2))
        // testing on an ILoWord where another word that's not the first
        // of ILoWord is active and matches the given String
        && t.checkExpect(this.list3.removeLetterActive("o"),
            new ConsLoWord(new ActiveWord("range", 20, 10),
                new ConsLoWord(this.red, new ConsLoWord(this.yellow, this.mt))));
  }

  // test for anyActive in an ILoWord
  boolean testanyActive(Tester t) {
    // testing anyActive on an empty list
    return t.checkExpect(this.mt.anyActive(), false)
        // testing on an ILoWord with both active and inactive words
        && t.checkExpect(this.animals7.anyActive(), true)
        // testing on an ILoWord with only inactive words
        && t.checkExpect(this.animals8.anyActive(), false)
        // testing on an ILoWord with only active words
        && t.checkExpect(this.list3.anyActive(), true);

  }

  // test for anyActiveHelper in an IWord
  boolean testActiveHelper(Tester t) {
    // testing anyActiveHelper on an ActiveWord
    return t.checkExpect(this.red.anyActiveHelper(), true)
        && t.checkExpect(this.orange.anyActiveHelper(), true)
        // testing anyActiveHelper on an InactiveWord
        && t.checkExpect(this.snake.anyActiveHelper(), false)
        && t.checkExpect(this.donkey.anyActiveHelper(), false);
  }

  // test for matchesStr in an IWord
  boolean testMatchesStr(Tester t) {
    // testing on an ActiveWord with a given String that matches first letter
    return t.checkExpect(this.red.matchesStr("r"), true)
        // testing on ActiveWord with an empty String
        && t.checkExpect(this.red.matchesStr(""), false)
        // testing on ActiveWord with more than 1 character
        && t.checkExpect(this.red.matchesStr("kr"), false)
        // testing on an ActiveWord with a given String that
        // doesn't match first letter
        && t.checkExpect(this.red.matchesStr("f"), false)
        // testing on an InactiveWord with a given String that matches first letter
        && t.checkExpect(this.snake.matchesStr("s"), true)
        // testing on InactiveWord with an empty String
        && t.checkExpect(this.snake.matchesStr(""), false)
        // testing on InactiveWord with more than 1 character
        && t.checkExpect(this.snake.matchesStr("na"), false)
        // testing on an InactiveWord with a given String that
        // doesn't match first letter
        && t.checkExpect(this.snake.matchesStr("false"), false);
  }

  // test for selectWord in an ILoWord
  boolean testSelectWord(Tester t) {
    // testing on an ILoWord that contains a word starting with the given String
    return t.checkExpect(this.animals7.selectWord("c"), true)
        // testing on an ILoWord that does not contain a word start with the give String
        && t.checkExpect(this.animals7.selectWord("x"), false)
        // testing with a String containing more then 1 character
        && t.checkExpect(this.animals7.selectWord("cx"), false)
        && t.checkExpect(this.mt.selectWord("x"), false);

  }
  
  //test for endOfGame
  boolean testEndOfGame(Tester t) {
    return
        //test empty case
        t.checkExpect(this.mt.endOfGame(), false)
        //test list with no words at the bottom
        && t.checkExpect(this.list6.endOfGame(), false)
        //test list with active word at the bottom
        && t.checkExpect(this.listAtBottom1.endOfGame(), true)
        //test list with inactive word at bottom
        && t.checkExpect(this.listAtBottom2.endOfGame(), true);
    
  }
  
  //test for anyEmpties
  boolean testAnyEmpties(Tester t) {
    return
        //test empty case
        t.checkExpect(this.mt.anyEmpties(), false)
        //test list with no words empty
        && t.checkExpect(this.list6.anyEmpties(), false)
        //test list with empties
        && t.checkExpect(this.list7.anyEmpties(), true);
  }
  
  //test for removeLetterHelp
  boolean testRemoveLetterHelp(Tester t) {
    return
        //tests empty case
        t.checkExpect(this.mt.addToEnd(this.red), new ConsLoWord(this.red, this.mt))
        //tests list
        && t.checkExpect(this.list3.removeLetterHelp(this.donkey), 
            new ConsLoWord(this.yellow,
                new ConsLoWord(this.orange,
                    new ConsLoWord(this.red,
                        new ConsLoWord(this.donkey, this.mt)))));
  }
  
  //test for removeLetter
  boolean testRemoveLetter(Tester t) {
    return
        //tests empty case
        t.checkExpect(this.mt.removeLetter("h"), this.mt)
        //tests list where letter must be removed
        && t.checkExpect(this.list3.removeLetter("r"), 
            new ConsLoWord(new ActiveWord("ed", 10, 45), 
                new ConsLoWord(this.yellow,
                    new ConsLoWord(this.orange, this.mt))));
                
  }
  
  //test for atBottom
  boolean testAtBottom(Tester t) {
    return
        //active word not at bottom
        t.checkExpect(this.red.atBottom(), false)
        //active word at bottom
        && t.checkExpect(this.white.atBottom(), true)
        //inactive word at bottom
        && t.checkExpect(this.horse.atBottom(), true)
        //inactive word not at bottom
        && t.checkExpect(this.donkey.atBottom(), false);
  }
  
  //test for removeLetterHelper
  boolean testRemoveLetterHelper(Tester t) {
    return
        //already active word
        t.checkExpect(this.red.removeLetterHelper(), new ActiveWord("ed", 10, 45))
        //already active word
        && t.checkExpect(this.white.removeLetterHelper(), new ActiveWord("hite", 30, 570))
        //inactive word 
        && t.checkExpect(this.horse.removeLetterHelper(), new ActiveWord("orse", 300, 570))
        //inactive word 
        && t.checkExpect(this.donkey.removeLetterHelper(), new ActiveWord("onkey", 10, 70)); 
  }
  
  // test for OnTickForTesting of a World
  boolean testOnTick(Tester t) {
    // testing on tick with no words displayed and with the ticks at an integer
    // that doesn't lead to the first case
    return t.checkExpect(this.world5.onTickForTesting(), 
        new ZTypeWorld(this.mt, new Random(2), 15 + 1, 0))
        // testing onTick with a few words displayed and with the ticks at an integer
        // that doesn't lead to the first case
        && t.checkExpect(this.world6.onTickForTesting(), 
            new ZTypeWorld(new ConsLoWord(new ActiveWord("orange", 20, 10 + 1), 
                new ConsLoWord(new ActiveWord("red", 10, 45 + 1), this.mt)), 
                new Random(2), 14 + 1, 5))
        // testing onTick on an ILoWord at the indicated tick where a new Random word
        // should be added onto the ZTypeWorld
        && t.checkExpect(this.world7.onTickForTesting(), 
            new ZTypeWorld(new ConsLoWord(new ActiveWord("orange", 20, 10 + 1), 
                new ConsLoWord(new ActiveWord("red", 10, 45 + 1), 
                    new ConsLoWord(new InactiveWord("clrrhs", 43, 0 + 1), this.mt))), 
                new Random(2), 20 + 1, 5));
        
  }

  //test for makeFinalScene
  boolean testMakeFinalScene(Tester t) {
    return
        //tests different worlds
        t.checkExpect(this.world5.makeAFinalScene(), 
            new WorldScene(400, 600).placeImageXY(
                new TextImage("Game Over", 20, Color.RED), 200 , 300).placeImageXY(
                    new TextImage("Score:" + 0, 20, Color.RED), 200 , 330))
        && t.checkExpect(this.world6.makeAFinalScene(), 
            new WorldScene(400, 600).placeImageXY(
                new TextImage("Game Over", 20, Color.RED), 200 , 300).placeImageXY(
                    new TextImage("Score:" + 5, 20, Color.RED), 200 , 330))
        && t.checkExpect(this.world7.makeAFinalScene(),
            new WorldScene(400, 600).placeImageXY(
                new TextImage("Game Over", 20, Color.RED), 200 , 300).placeImageXY(
                    new TextImage("Score:" + 5, 20, Color.RED), 200 , 330));
  }
  
  /*
  //test for big bang
  boolean testBigBang(Tester t) {
    ZTypeWorld world = new ZTypeWorld(this.mt, new Random(), 0, 0);
    int worldWidth = 400;
    int worldHeight = 600;
    double tickRate = .05;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }
  */
}