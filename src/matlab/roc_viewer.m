% plots the ROC curve for a set of trials
% for each file, for each threshold, for each class 
% the tp/fp/tn/fn values are calculated
% threshold values are averaged into the nearest step value
% the end result is a set of N graphs, each corresponding to a trial
% the set of N graphs is averaged into one single ROC curve

clear;
files = uipickfiles('FilterSpec', '/Work/msc/code/malleterisk-test/results');

num_samples = 20;
step = 1/num_samples;
num_samples = num_samples + 1; % fix this value, since fpr will be +1
ones_aux = ones(num_samples, 1); % vector used for internal computations

fpr = (0:step:1)';
tpr_sum = zeros(num_samples,1);
tpr_n = zeros(num_samples,1);
tpr_max = zeros(num_samples,1); 
tpr_min = ones(num_samples,1);

figure;
for i=1:length(files)
    % load and format data (only retrieve specific columns)
    data = importdata([files{i}], ',');
    % true classes
    real_class_indices = data.data(:, 1:2); % 1: indice, 2: label
    % data of all classes
    l0 = data.data(:, 5:end);
    
    n_lines = size(l0,1);
    n_columns = size(l0, 2);

    % current file's information
    current_file_tpr_sum = zeros(num_samples,1);
    current_file_tpr_n = zeros(num_samples,1);

    for j=1:3:n_columns % iterate all classes
        % get values for current class
        class = l0(:, j);
        values = l0(:, j+2);

        % lines that are equal to the current class
        rp = (real_class_indices(:, 1) == class);
        rn = (real_class_indices(:, 1) ~= class);

        for k=1:n_lines% iterate all instances
            t = values(k); % threshold
            
            % classification results
            p = (values >= t);
            n = (values < t);
            np = sum(p);
            nn = sum(n);

            % true/false positives/negatives
            tp = sum(rp==p & p==1);
            fp = sum(rn==p & p==1);
            tn = sum(rn==n & n==1);
            fn = sum(rp==n & n==1);

            x = fp / (tn + fp);
            y = tp / (tp + fn);
            if isnan(x)
                x = 0;
            end
            if isnan(y)
                y = 0;
            end
            
            % find the idx aka nearest_fpr to x
            % calculate the difference of fpr to x and find the last indice
            % (when the distance is the same to 2 points, use the highest value)
            dif = abs(fpr - (ones_aux*x));
            idx = find(dif == min(dif), 1, 'last' );
            
            current_file_tpr_sum(idx, 1) = current_file_tpr_sum(idx, 1) + y;
            current_file_tpr_n(idx, 1) = current_file_tpr_n(idx, 1) + 1;
        end
    end
    
    % plot current file
    current_file_tpr_avg = current_file_tpr_sum ./ current_file_tpr_n;
    plot(fpr, current_file_tpr_avg);
    hold on;
    
    % add to global average
    tpr_sum = tpr_sum + current_file_tpr_sum;
    tpr_n = tpr_n + current_file_tpr_n;
    
    % update max/min for errorbar
    % this only works for more than one file
    for j=1:num_samples
        y = current_file_tpr_avg(j, 1);
        
        if y > tpr_max(j, 1)
            tpr_max(j, 1) = y;
        end
        if y < tpr_min(j, 1)
            tpr_min(j, 1) = y;
        end
    end
end

% compute final average
tpr_avg = tpr_sum ./ tpr_n;
errorbar(fpr, tpr_avg, tpr_max-tpr_min, '--s', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'g', 'MarkerSize', 10);
%plot(fpr, tpr_avg, '--s', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'g', 'MarkerSize', 10);
axis([0 1 0 1]); xlabel('FP Rate'); ylabel('TP Rate');
