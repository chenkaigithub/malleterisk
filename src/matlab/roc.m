clear;
%[file_name, path_name, filter_index] = uigetfile('*.*');
path_name = '/Work/msc/code/seamce-test/';
file_name = 'trial+instances+1+6+subjects+RandomSampler+1188+FeatureWeighting-TF-IDF+FilterByRankedIG+122+NaiveBayesTrainer+2011-05-08-02-21-44';

% load data
data = importdata([path_name file_name], ',');
instances = data.textdata(:, 1);

% format data (only retrieve specified columns)
real_class_indices = data.data(:, 1:2);

% retrieve best class indices
% values are not ordered
l0 = data.data(:, 5:end);
n_lines = size(l0,1);
n_columns = size(l0, 2);

for i=1:3:n_columns
    classes = l0(:, i);
    values = l0(:, i+2);
    
    % lines that are equal to the current class
    rp = (real_class_indices(:, 1) == classes);
    rn = (real_class_indices(:, 1) ~= classes);

    for t=0.1:0.1:0.9
        % classification results
        p = (values >= t);
        n = (values < t);
        np = sum(p);
        nn = sum(n);

        % true/false positives/negatives
        tp = 0;
        fn = 0;
        fp = 0;
        tn = 0;

        
        for l=1:n_lines
            if rp(l) == 1
                if p(l) == 1
                    tp = tp + 1;
                else
                    fn = fn + 1;
                end
            else
                if p(l) == 1
                    fp = fp + 1;
                else
                    tn = tn + 1;
                end
            end
        end

        x = fp / (tn + fn);
        y = tp / (tp + fp);

        plot(x, y, 'o'); hold on;
    end
end

% for i=1:3:n_columns
%     classes = l0(:, i);
%     values = l0(:, i+2);
%     
%     for t=0.1:0.1:0.9
%         positives = values >= t;
%         negatives = values < t;
%         p = sum(positives); % number of positives
%         n = sum(negatives); % number of negatives
%         
%         rp = (real_class_indices(:, 1) == classes); % real positives (all instances belonging to the class)
%         rn = (real_class_indices(:, 1) ~= classes); % real negatives (all instances not belonging to the class)
%         
%         fp = sum(rn==positives); % number of false positives
%         tp = p-fp; % number of true positives
%         fn = sum(rp == negatives); % number of false negatives
%         tn = n-fn; % number of true negatives
%         y = tp / p;
%         x = fp / n;
%         
%         plot(x, y, 'o'); hold on;
%     end
% end
