---
layout: post
title: "Meta Is Better: Musings on RPA"
---

<em>“Worse is better” - Ancient Proverb</em>


Admittedly, I only heard about RPA recently while browsing around, but it caught my eye. Companies like [UiPath](https://www.uipath.com/) have experienced explosive [growth](https://techcrunch.com/2019/06/24/gartner-finds-rpa-is-fastest-growing-market-in-enterprise-software/). And there is reason to believe that the time is ripe for “Robotic Process Automation” solutions. But why now, and what makes this automation the sort that actually happens and isn’t perpetually on the horizon?

RPA is living up to its promise because it is a <em>metainterface</em>: it creates new opportunity for automation through increasing interoperability of systems. Software is now old enough as an industry tool that clunky out-dated legacy systems and external systems without good application programming interfaces lurk in the spines of many a organization, creating procedural and operational drag in a variety of firm functions. Legacy systems or external systems that don’t have welcoming API’s with which to conduct scalable operations make for higher cost curves to what automation opportunities are available, limiting what is actually worth automating. As a result, it is difficult or expensive to automate processes in settings where not all (or any) pieces of the flow are using software applications with APIs. A manual process may start from legacy software with only a GUI, to another application with an API available if needed, and back to GUI-only legacy. Meta is better. By making an interface for your interfaces – effectively by making all interfaces programmable – RPA enables proper automation for procedures that would have been unfeasible, cumbersome, or too costly to perform before.

Changing cost curves for automating processes means changing the firm’s internal dynamics. By using programming-by-demonstration and drag-and-drop paradigms in tools like “UiPath Studio”, making RPA workflows becomes a cross between using an Adobe product and more raw forms of programming like VBA and Python. Tools designed like UiPath Studio enable non- or semi- technical users to transition into RPA development much more quickly than if they were to attempt to transition into software engineers, which in turn, means that companies can quickly adopt RPA internally via some training and deployment and begin reaping benefits rapidly. Additionally, RPA is often even leveraged to offload work from engineering departments where development time can be highly valued as a means of delivering on the core value proposition of a software product. Instead of having scarce engineering resources expended helping to create some internal tooling, often RPA can be an alternative solution which can be quickly and cheaply (in terms of firm resources) developed and deployed.



![Histogram of Business Process Automation Reward/Cost Ratio](/assets/rpa.png)



One initial skepticism I had about RPA was what is called Process Discovery: how to identify processes that are high reward/cost ratio? And how costly or difficult would the actual process discovery be in practice – especially in larger and older organizations? It turns out that with sufficient initial support from the Top (where the imitative for RPA seems to often times originate, anyways) this isn’t particularly difficult for a firm with some dedication to the task. And - perhaps in an appropriate twist - UiPath has already started developing a [tool](https://www.uipath.com/product/process-understanding-explorer) to help automate the process of discovering processes that are good candidates for automation. 

There are some downsides to RPA as one might expect. For example: RPA processes can be fragile and prone to depreciating due to the underlying applications/interfaces evolving – which especially the case with UI/UX redesigns from external systems that are part of a workflow (its probably less common for APIs to be depreciated or changed). Another difficulty is simply that many business processes contain large numbers of possible exceptions that can occur at any given process step that need to be navigated: this the kind of thing that can be annoying or cumbersome to deal with and can take more time and effort to automate end-to-end, which can limit RPA’s effectiveness in some areas. However, even here, [human-in-the-loop](https://www.youtube.com/watch?v=yoxz_DV0BIU) approaches are being leveraged here to bring automation capabilities right to the edge. RPA – or automation, in general – isn’t solely about cost reduction, but simultaneously augmenting productivity. 

Depending on your point of view, RPA can seem like a clever and elegant solution to a broad class of problems, or a glossy hack over the woeful state of software and tools. Nevertheless the space is growing, alongside UiPath, Automation Anywhere and others, there are tools like Zapier and automate.io, and even android apps like Automate (which I’ve heard are sometimes used in click farms).

Software eating the world didn’t mean that interoperability would be a free lunch. But now, at least, you can pay for it.
