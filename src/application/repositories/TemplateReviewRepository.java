package application.repositories;

import java.util.List;

import application.entities.TemplateReview;
import application.entities.TemplateReview.Period;

public interface TemplateReviewRepository {

    public TemplateReview findById(String id);

    public List<TemplateReview> listByPeriod(Period period);

    public List<TemplateReview> listAll();

    public void add(TemplateReview templateReview);

    public void update(TemplateReview templateReview);
}
