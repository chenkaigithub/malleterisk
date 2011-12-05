becks = [.58, .17 .59 .76 .58 .4]
farmerd = [.77 .56 .76 .81 .79 .69]
kaminskiv = [.66 .27 .67 .72 .7 .48]
kitchenl = [.53 .25 .54 .85 .55 .34]
lokaym = [.79 .61 .78 .86 .83 .73]
sandersr = [.78 .52 .75 .8 .81 .62]
williamsw3 = [.92 .91 .93 .94 .89 .88]
insticc = [.81 .56 .82 .86 .75 .62]

x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('Balanced Winnow', 'Decision Tree', 'SVMs', 'Max Entropy', 'Naive Bayes', 'Winnow')
