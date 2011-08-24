package types.mallet.pipe;

import java.io.Serializable;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class PorterStemmerSequence extends Pipe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Instance pipe(Instance carrier) {
		final PorterStemmer porterStemmer = new PorterStemmer();
		
		TokenSequence ts = (TokenSequence) carrier.getData();
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			
			porterStemmer.add(t.getText());
			porterStemmer.stem();
			
			t.setText(porterStemmer.toString());
		}
		
		return carrier;
	}	
}
