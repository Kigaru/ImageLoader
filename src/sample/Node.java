package sample;

public class Node<T> {

	private T contents; //the reference to the data type.
	private Node<T> next;
	private Node<T> previous;
	
	public T getContents() {
		return contents;
	}


	public void setContents(T contents) {
		this.contents = contents;
	}


	public Node<T> getNext() {
		return next;
	}


	public void setNext(Node<T> next) {
		this.next = next;
	}


	public Node<T> getPrevious() {
		return previous;
	}


	public void setPrevious(Node<T> previous) {
		this.previous = previous;
	}
	
	
}

