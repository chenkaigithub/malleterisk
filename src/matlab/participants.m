participants = load('participants');
email_ids = unique(participants(:, 1));
x = participants(find(participants(:,1) == 1), :);
y = x(:, 2)';
