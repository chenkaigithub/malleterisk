function cm = confusion_matrix(file_name)
    data = importdata(file_name, ',');
    
    % format data (only retrieve specified columns)
    real_class_indices = data.data(:, 1);   % the class "name" (index)
    real_class_split = data.data(:, 3:4);   % # train and test instances

    % for each instance, retrieve best class indices (values are not ordered)
    l0 = data.data(:, 5:end);
    n_lines = size(l0,1);
    best_class_indices = zeros(n_lines, 1);
    for i=1:n_lines
        % build triplets (class_idx, class_label, class_value)
        l1 = l0(i, :);
        l2 = reshape(l1, 3, size(l1, 2)/3)';
        % sort by value (ascending) and get last triplet's class_idx
        l3 = sortrows(l2, 3);
        l4 = l3(size(l3,1), 1);
        best_class_indices(i, 1) = l4;
    end

    % compute confusion matrix
    [cm, order] = confusionmat(real_class_indices, best_class_indices);
    
    % normalize values by number of test documents
    for i=1:size(cm, 1)
        cm(i,:) = cm(i,:) / sum(cm(i,:));
    end

    cm(isnan(cm)) = 0; % replace NaNs with 0s

