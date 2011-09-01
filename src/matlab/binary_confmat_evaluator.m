clear;
files = uipickfiles('FilterSpec', '/Work/msc/code/malleterisk-test/results');
num_files = size(files, 1);

res_cm = zeros(2, 2);
for i = 1:num_files
    cm = confusion_matrix(files{i});
    res_cm = res_cm + cm;
end

res_cm / num_files