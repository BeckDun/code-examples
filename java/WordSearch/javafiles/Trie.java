package javafiles;

/**
 * Trie data stucture. Uses a character array of 26 nodes.
 * Only applicable for searching through lowercase letters.
 * Inserts a lowercase word.
 */
public class Trie {
    private Node root;

    /**
     * Constructor method for a Trie.
     */
    public Trie() {
        root = new Node('\0');
    }

    /**
     * Inserts a word into the trie by splitting a word and looping through.
     * Assigns the current node to whatever the current char is.
     * @param word Word to insert into the trie.
     */
    public void insert(String word) {
        Node currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (currentNode.children[c - 'a'] == null) {
                currentNode.children[c-'a'] = new Node(c);
            }
            currentNode = currentNode.children[c-'a'];
        }
        currentNode.isWord = true;
    }

    /**
     * searches for the prefix in the Trie
     * @param word passed a word, or partial word wanting to be found
     * @return returns if the word passed is in the Trie.
     */
    public Boolean searchPrefix(String word) {
        Node temp = this.root;
        for (int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            Node next = temp.children[c - 'a'];
            if (next == null) {
                return false;
            }
            temp = next;
        }
        return !(temp == null) || temp.isWord;
    }

    /**
     * searches for the prefix in the Trie
     * @param iterator passed a Iterator with characters
     * @return returns if the word passed is in the Trie.
     */
    public Boolean search(Iterator<Character> iterator) {
        Node temp = this.root;

        while (iterator.hasNext()) {
            char c = iterator.next();
            Node next = temp.children[c - 'a'];
            if(next == null) {
                return false;
            } else if(next.isWord) {
                return true;
            }
        }
        return false;
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
     * @return
     */
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    private Node getNode ( String word) {
        Node currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if(currentNode.children[c-'a'] == null) {
                return null;
            }
            currentNode = currentNode.children[c-'a'];
        }
        return currentNode;
    }
    /**
     * Internal node class used by the Trie to organize letters 
     */
    private class Node {
        public char c;
        public boolean isWord;
        public Node[] children;

        /**
         * Node constructor class
         * @param c takes a character to create a new node
         */
        public Node(char c) {
            this.c = c;
            isWord = false;
            children = new Node[26];
        }
    }
}
