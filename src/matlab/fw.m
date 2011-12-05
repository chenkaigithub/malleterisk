becks = [0.5 0.45 0.57 0.57 0.22 0.32]
farmerd = [0.77 0.76 0.77 0.77 0.53 .66]
kaminskiv = [.67 .62 .69 .64 .32 .48]
kitchenl = [.49 .45 .55 .47 .23 .33]
lokaym = [.82 .8 .82 .81 .55 .73]
sandersr = [.7 .64 .77 .66 .42 .51]
williamsw3 = [.9 .9 .87 .9 .87 .9]
insticc = [.78 .77 .73 .78 .57 .7]
x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('TF','Boolean','TF-IDF','TF-Log', 'TF-IDF ltc','TF-Max-Norm')