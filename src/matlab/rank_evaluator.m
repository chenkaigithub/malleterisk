clear;
files = uipickfiles('FilterSpec', '/Work/msc/code/malleterisk-test/results');
num_files = length(files);

table = [];
for i = 1:num_files
    % load data
    data = importdata([files{i}], ',');
    instances = data.textdata(:, 1);

    % format data (only retrieve specified columns)
    real_class_indices = data.data(:, 1);

    l0 = data.data(:, 5:end);
    n_lines = size(l0,1);
    best_class_indices = zeros(n_lines, 3); % the indices of the best classified classes
    real_class_rank = zeros(n_lines, 1);    % the rank (position) of the real class
    real_class_values = zeros(n_lines, 1);
    for j=1:n_lines
        % build triplets (class_idx, class_label, class_value)
        l1 = l0(j, :);
        l2 = reshape(l1, 3, size(l1, 2)/3)';
        % sort by value and the last triplet's class_idx
        l3 = sortrows(l2, -3);
        best_class_indices(j, :) = l3(1, :);
        real_class_rank(j, 1) = find(l3(:, 1) == real_class_indices(j, 1));
        real_class_values(j, 1) = l3(real_class_rank(j,1), 3);
    end
    
    % best class, value | real class, value | rank of real class in classification
    %horzcat(best_class_indices(:, 1), best_class_indices(:, 3), real_class_indices, real_class_values, real_class_rank)
    
    table = [table; horzcat(best_class_indices(:, 1), real_class_indices, real_class_rank)];
end

sum(table(:, 3) < 2) / size(table, 1)
sum(table(:, 3) < 4) / size(table, 1)
