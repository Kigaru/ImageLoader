package sample;

public class LinkedList<T> {

	private Node<T> head = null;
	private Node<T> tail = null;
	private int length = 0;
	
	
	public void insertElement(T obj) { //add element to the head
		Node<T> j = new Node<>();
		j.setContents(obj);
		j.setNext(head); //next item in the list is the head of..
		head = j; //..this item
		j.setPrevious(null);
		if(tail!=null) head.getNext().setPrevious(j);
		else tail = j;
		length++;
	}
	
	public void insertLastElement(T obj) { //add element to the end
		if(head!=null) {
			Node<T> temp = head;
			while(temp.getNext()!=null) {
				temp = temp.getNext(); //iterating through the list until the end is reached	
			}
			Node<T> j = new Node<>();
			j.setContents(obj);
			temp.setNext(j);
			j.setPrevious(temp);
			tail = j; // this element is the new tail
			length++;
		}
		else insertElement(obj);
	}
	
	public void insertElementWithin(T obj, int index) { //insert an element at a specific index, between two elements
		if(head!=null) {
			if(index<length) {
				Node<T> temp = head;
				for(int i=0;i<index;i++) {
					temp = temp.getNext(); //iterating through the list until at the required index	
				}
				Node<T> j = new Node<>();
				j.setPrevious(temp.getPrevious());
				temp.getPrevious().setNext(j);
				temp.setPrevious(j);
				j.setNext(temp);
			}
			else {}
		}
		else insertElement(obj);
	}
	
	public void deleteElement(int index) { //remove at a specific index
		if(head!=null) {
			if(index<=length) {
				Node<T> temp = head;
				for(int i=0;i<index;i++) {
					temp = temp.getNext(); //iterating through the list until at the required index	
				}
				if(temp.getPrevious()==null) { //for a first that doesn't have a previous
					if(temp.getNext()==null) { //if it's the only one in the list then clear it
						clear();
					}
					else {
						System.out.println(temp.getNext());
						head = temp.getNext();
						temp.getNext().setPrevious(null);
						length--;
					}
				}
				else if(temp.getNext()!=null) { //for non-first that has a next
					temp.getPrevious().setNext(temp.getNext());
					temp.getNext().setPrevious(temp.getPrevious());
					length--;
				}
				else { //deleting the last one
					tail = temp.getPrevious();
					temp.getPrevious().setNext(null);
					length--;
				}
			}
			else {}
		}
		else {}
	}
	
	public T getFirst() { //get the first element in the list
		return head.getContents();
	}
	
	public T getLast() { //get the last element in the list
		return tail.getContents();
	}
	
	public T get(int index) { //get the contents of an element
		if(head!=null) {
			if(index<=length) {
				Node<T> temp = head;
				for(int i=0;i<index;i++) {
					temp = temp.getNext(); //iterating through the list until at the required index	
				}
				return temp.getContents();
			}
			else {throw new IndexOutOfBoundsException("Index out of bounds!");}
		}
		else {throw new ArrayIndexOutOfBoundsException("No Content");}
	}
	
	public void set(T obj, int index) { //updates the element at an index
		if(index<=length) {
			Node<T> temp = head;
			for(int i=0;i<index;i++) {
				temp = temp.getNext(); //iterating through the list until at the required index	
			}
			temp.setContents(obj);
		}
		else {
			if(head==null)
				throw new NullPointerException("No content");
			
			else 
				throw new IndexOutOfBoundsException("Index needs to be between 0 and " + length);}
		
	}
	
	public void clear() { //to delete the whole list and orphan every node
		head=null;
		tail=null;
		length = 0;
	}

	public int size() {
		return length;
	}

	public T next(T obj) {
		if(head!=null) {
			Node<T> temp = head;
			for(int i=0;i<length-1;i++) {
				if(temp.getContents() == obj) {
					return temp.getNext().getContents();
				}
				temp = temp.getNext();
			}
		}
		else {}
			return null;
	}
}
