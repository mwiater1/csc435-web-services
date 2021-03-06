<div class="row">
    <#list sources as s>
    <div class="col s12 m6 l4">
        <div class="card large hoverable">
            <div class="card-content">
                <span class="card-title grey-text text-darken-4">${s.name}</span>
                <p>${s.description}</p>
                <ul class="collection">
                    <li class="collection-item"><div>Category<span class="new badge" data-badge-caption="${s.category}"></span></div></li>
                    <li class="collection-item"><div>Language<span class="new badge" data-badge-caption="${s.language}"></span></div></li>
                    <li class="collection-item"><div>Country<span class="new badge" data-badge-caption="${s.country}"></span></div></li>
                </ul>
                <a href="${s.url}" class="waves-effect waves-light btn">Go To Site</a>
            </div>
            <div class="card-action">
                <#list s.sortBysAvailable as b>
                <a href="${ "/hw7/articles?source=" + s.id + "&sortby=" + b }" class="waves-effect waves-light btn">${b}</a>
                </#list>
            </div>
        </div>
    </div>
    </#list>
</div>