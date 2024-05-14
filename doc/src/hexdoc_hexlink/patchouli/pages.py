from hexdoc.patchouli.page import PageWithTitle


class ExamplePage(PageWithTitle, type="hexlink:example"):
    """This is the Pydantic model for the `hexlink:example` page type."""

    example_value: str
