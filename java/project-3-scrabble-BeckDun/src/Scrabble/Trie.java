package Scrabble;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Trie data stucture. Uses a character array of 26 nodes.
 * Only applicable for searching through lowercase letters.
 * Inserts a lowercase word.
 * @author BeckDun
 */
public class Trie {
    /** The root node of the trie */
    public final Node root = new Node('\0');

    /**
     * Inserts a word into the trie by splitting a word and looping through.
     * Assigns the current node to whatever the current char is.
     * @param word Word to insert into the trie.
     */
    public void insert(String word) {
        Node current = root;

        for (Character l: word.toCharArray()) {
            current = current.children.computeIfAbsent(l, c -> new Node(c));
        }
        current.isWord = true;
    }

    /**
     * searches for the prefix in the Trie
     * @param word passed a word, or partial word wanting to be found
     * @return returns if the word passed is in the Trie.
     */
    public Boolean searchPrefix(String word) {
        Node temp = this.root;
        for (int i = 0; i < word.length(); i++){
            Character c = word.charAt(i);
            Node next = temp.children.get(c);
            if (next == null) {
                return false;
            }
            temp = next;
        }
        return !(temp == null) || temp.isWord;
    }

    /**
     * searches for the word in the Trie
     * @param iterator passed an Iterator with characters
     * @return returns if the word passed is in the Trie.
     */
    public Boolean search(Iterator<Character> iterator) {
        Node temp = this.root;

        while (iterator.hasNext()) {
            Character c = iterator.next();
            Node next = temp.children.get(c);
            if(next == null) {
                return false;
            } else if(next.isWord) {
                return true;
            }
        }
        return false;
    }

    /**
     * searches for the prefix in the Trie
     * @param iterator passed an Iterator with characters
     * @return returns if the prefix passed is in the Trie.
     */
    public Boolean validPrefix(Iterator<Character> iterator) {
        Node temp = this.root;

        while (iterator.hasNext()) {
            Character c = iterator.next();
            Node next = temp.children.get(c);
            if(next == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches for a word inside the Trie.
     * @param word Word given
     * @return returns boolean if the string passed to method is a word in Trie
     */
    public boolean searchForWord(String word) {
        Node node = getNode(word);
        return node != null && node.isWord;
    }

    /**
     * Method to see if any nodes start with the prefix passed to the method.
     * @param prefix prefix string
     * @return true if the prefix exists in the trie, false otherwise
     */
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    /**
     * Gets the node in the trie corresponding to the last character of the given word.
     *
     * @param word The word to search for
     * @return The node corresponding to the last character, or null if not found
     */
    public Node getNode(String word) {
        Node currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            if(currentNode.children.get(c) == null) {
                return null;
            }
            currentNode = currentNode.children.get(c);
        }
        return currentNode;
    }

    /**
     * A node in the trie data structure.
     * Each node represents a character and contains children nodes.
     */
    public class Node {
        /** The character this node represents */
        public char c;

        /** Flag indicating if this node is the end of a valid word */
        public boolean isWord;

        /** Map of child nodes, keyed by character */
        public HashMap<Character, Node> children;

        /**
         * Node constructor class
         * @param c takes a character to create a new node
         */
        public Node(char c) {
            this.c = c;
            isWord = false;
            children = new HashMap<>();
        }
    }
}