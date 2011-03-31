function xnorm = normalization(x)
    n = sum(x, 2);
    
    xnorm = zeros(size(x));
    for i=1:length(n)
        xnorm(i,:) = x(i,:) / n(i);
        xnorm(i,:) = x(i,:) / n(i);
    end