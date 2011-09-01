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

    % calculate class sizes
    rc = unique([real_class_indices real_class_split], 'rows');
    sizes = zeros(size(order, 1), 2);
    for i=1:size(rc, 1)
        idx = find(order == rc(i, 1));
        sizes(idx, :) = rc(i, 2:3);
    end
    p = [sizes(:, 1) sizes(:, 2)]; % training documents size; test documents size
    %p = [sum(cm,2) sum(cm,2)];
    p_x = 1:size(p, 1);

    % normalize values by number of test documents
    for i=p_x
        cm(i,:) = cm(i,:) / p(i, 2);
    end
