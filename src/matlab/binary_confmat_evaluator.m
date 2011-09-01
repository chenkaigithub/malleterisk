clear;
files = uipickfiles('FilterSpec', '/Work/msc/code/malleterisk-test/results');
num_files = size(files, 2);

res_cm = zeros(2, 2);
n = 0;
for i = 1:num_files
    cm = confusion_matrix(files{i});
    
    % ignoring folds that have only one class
    % can occur when the single class was correctly classified (scalar)
    % or when single class was not correctly classified
    % must check both cases since correct classification under single
    % class is a special case
    if length(cm) > 1 && sum(cm(1,:)) == 1
        res_cm = res_cm + cm;
        n = n + 1;
    end
end

res = res_cm / n;
