package simpledb;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class HeapFileIterator implements DbFileIterator {

	
	private int tableid, pgNo;
	private TransactionId transid;
	private ListIterator<Tuple> tuples;
	
	public HeapFileIterator(TransactionId transid, int tableid){
		this.tableid = tableid;
		this.transid = transid;
		pgNo = 0;
	}
	
	
	@Override
	public void open() throws DbException, TransactionAbortedException {
		PageId pgId = new HeapPageId(tableid, pgNo);
    	HeapPage hp = (HeapPage)Database.getBufferPool().getPage(transid, pgId, Permissions.READ_ONLY);
    	pgNo++;
    	tuples = (ListIterator<Tuple>) hp.iterator();
	}

	@Override
	public boolean hasNext() throws DbException, TransactionAbortedException {
		if (tuples == null)
			return false;
		HeapFile file = (HeapFile)Database.getCatalog().getDatabaseFile(tableid);
		return (tuples.hasNext() || pgNo < file.numPages());
	}

	@Override
	public Tuple next() throws DbException, TransactionAbortedException,
			NoSuchElementException {
		if (tuples == null)
			throw new NoSuchElementException();
		HeapFile file = (HeapFile)Database.getCatalog().getDatabaseFile(tableid);
		if (!tuples.hasNext() && pgNo < file.numPages()){
			open();
		}
		return tuples.next();
	}

	@Override
	public void rewind() throws DbException, TransactionAbortedException {
		pgNo = 0;
		open();
	}

	@Override
	public void close() {
		tuples = null;
		this.pgNo = 0;
	}
}