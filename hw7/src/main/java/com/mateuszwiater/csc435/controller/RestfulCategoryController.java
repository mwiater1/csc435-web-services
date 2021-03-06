package com.mateuszwiater.csc435.controller;

import com.mateuszwiater.csc435.model.Category;
import com.mateuszwiater.csc435.view.CategoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.mateuszwiater.csc435.util.Response.Status.FAIL;
import static com.mateuszwiater.csc435.util.Response.Status.OK;

public class RestfulCategoryController {
    private static Logger logger = LoggerFactory.getLogger(RestfulCategoryController.class);

    public static Route GET = (req, resp) -> {
        try {
            return CategoryView.getView(OK,"", Category.getCategories());
        } catch (SQLException e) {
            logger.error("CATEGORY GET", e);
            resp.status(500);
            return CategoryView.getView(FAIL, "Internal Server Error", new ArrayList<>());
        }
    };
}
