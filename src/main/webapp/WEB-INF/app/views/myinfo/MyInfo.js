import React, { useContext } from "react"
import { ThemeContext } from "app/contexts/ThemeContext";


export default function MyInfo() {

  // const theme = useContext(ThemeContext)
  return (
    <div className="flex flex-row justify-between">
      <NavigationMenu>
        <Hero>
          My Info Menu
        </Hero>
        Paycheck
      </NavigationMenu>
      <AppContent>
        <Card>
          <Hero>
            Kevin
          </Hero>
          <div className="p-3">
            <StubText/>
          </div>
        </Card>
      </AppContent>
    </div>
  )
}

function NavigationMenu({ children }) {
  return (
    <nav className="w-[250px] flex-none">
      <Card>
        {children}
      </Card>
    </nav>
  )
}

function AppContent({ children }) {
  return (
    <div className="w-[880px] flex-none">
      {children}
    </div>
  )
}

function Card({ children }) {
  return (
    <div className="bg-white shadow">
      {children}
    </div>
  )
}

function Hero({ children }) {
  return (
    <div className="px-3 py-2 bg-blue-800 text-base text-white font-medium">
      {children}
    </div>
  )
}

function StubText() {
  return (
    <div>
      Basic information
      <br/>
      Shortcut
      <br/>
      WP:STUBDEF
      <br/>
      A stub is an article that, although lacking the breadth of coverage expected from an encyclopedia, provides some
      useful information and is capable of expansion. Non-article pages, such as disambiguation pages, lists,
      categories, templates, talk pages, and redirects, are not regarded as stubs.

      If a stub has little verifiable information, or if its subject has no apparent notability, it may be deleted or be
      merged into another relevant article.

      While a "definition" may be enough to qualify an article as a stub, Wikipedia is not a dictionary. The distinction
      between dictionary and encyclopedia articles is best expressed by the use–mention distinction:

      A dictionary article is about a word or phrase and will often have several different definitions for it
      An encyclopedia article is about the subject denoted by the title but usually has only one definition (or in some
      cases, several definitions that are largely the same) but there may be several equivalent words (synonyms) or
      phrases for it.
      Sizable articles are usually not considered stubs, even if they have significant problems or are noticeably
      incomplete. With these larger articles, a cleanup template is usually added instead of a stub template.

      How big is too big?
      Over the years, different editors have followed different rules of thumb to help them decide when an article is
      likely to be a stub. Editors may decide that an article with more than ten sentences is too big to be a stub, or
      that the threshold for another article may be 250 words. Others follow the Did you know? standard of 1,500
      characters in the main text. AutoWikiBrowser is frequently set to automatically remove stub tags from any article
      with more than 500 words.

      There is no set size at which an article stops being a stub. While very short articles are very likely to be
      stubs, there are some subjects about which very little can be written. Conversely, there are subjects about which
      a lot could be written, and their articles may still be stubs even if they are a few paragraphs long. As such, it
      is impossible to state whether an article is a stub based solely on its length, and any decision on the article
      has to come down to an editor's best judgment (the user essay on the Croughton-London rule may be of use when
      trying to judge whether an article is a stub). Similarly, stub status usually depends on the length of prose text
      alone; lists, templates, images, and other such peripheral parts of an article are usually not considered when
      judging whether an article is a stub.

      Creating and improving a stub article
      Shortcuts
      WP:PSA
      WP:IDEALSTUB
      See also: Wikipedia:Writing better articles
      A stub should contain enough information for other editors to expand upon it. The key is to provide adequate
      context—articles with little or no context usually end up being speedily deleted. Your initial research may be
      done either through books or reliable websites. You may also contribute knowledge acquired from other sources, but
      it is useful to conduct some research beforehand to ensure that your facts are accurate and unbiased. Use your own
      words: directly copying other sources without giving them credit is plagiarism, and may in some cases be a
      violation of copyright.

      Begin by defining or describing your topic. Avoid fallacies of definition. Write clearly and informatively. State
      what a person is famous for, where a place is located and what it is known for, or the basic details of an event
      and when it happened.

      Next, try to expand upon this basic definition. Internally link relevant words, so that users unfamiliar with the
      subject can understand what you have written. Avoid linking words needlessly; instead, consider which words may
      require further definition for a casual reader to understand the article.

      Lastly, a critical step: add sources for the information you have put into the stub; see citing sources for
      information on how to do so in Wikipedia.

      How to mark an article as a stub
      Shortcuts
      WP:STUBSPACING
      WP:SVSP
      WP:TAGSTUB
      After writing a short article, or finding an unmarked stub, you should insert a stub template. Choose from among
      the templates listed at Wikipedia:WikiProject Stub sorting/Stub types, or just use the generic , which others can
      sort later. Stubs should never be manually added to stub categories—always use a template.

      Per the Manual of Style, the stub template is placed at the end of the article, after the External links section,
      any navigation templates, and the category tags, so that the stub category will appear after all article content.
      Leave two blank lines between the first stub template and whatever precedes it. (One blank line leaves the stub
      category notice butted up against any preceding navigation template, it takes two blank lines in the edited text
      to produce one blank line in the displayed text.) As with all templates, stub templates are added by simply
      placing the name of the template in the text between double pairs of curly brackets (eub templates are
      transcluded, not substituted.

      Stub templates have two parts: a short message noting the stub's topic and encouraging editors to expand it, and a
      category link, which places the article in a stub category alongside other stubs on the same topic. The naming for
      stub templates is usually topic-stub; a list of these templates may be found here. You need not learn all the
      templates—even simply a helps (see this essay for more information). The more accurately an article is tagged,
      however, the less work it is for other sorters later, and the more useful it is for editors looking for articles
      to expand.

      If a more specific stub template than is currently on an article exists and completely covers the subject of the
      article, remove the more general template and replace it with the more specific type (for example, an article on
      Morocco may be stubbed with. If it is solely about Morocco, remove the template and replace it with specific
      template can often replace multiple more general types (for exa

      If an article overlaps several stub categories, more than one template may be used, but it is strongly recommended
      that only those relating to the subject's main notability be used. A limit of three or, if really necessary, four
      stub templates is advised.

      Stub-related activities are centralised at Wikipedia:WikiProject Stub sorting (shortcut Wikipedia:WSS). This
      project should be your main reference for stub information, and is where new stub types should be proposed for
      discussion prior to creation.

      Removing stub status
      Shortcut
      WP:DESTUB
      Once a stub has been properly expanded and becomes a larger article, any editor may remove its stub template. No
      administrator action or formal permission is needed. Stub templates are usually located at the bottom of the page,
      and usually have a name like if you are using the classic wikitext editor rather than VisualEditor.

      Many articles still marked as stubs have in fact been expanded beyond what is regarded as stub size. If an article
      is too large to be considered a stub but still needs expansion, the stub template may be removed and
      appropriateemplates may be added (no article should contain both a stub template and an expand template).

      When removing stub templates, users should also visit the talk page and update the WikiProject classifications as
      necessary.

      Be bold in removing stub tags that are clearly no longer applicable.

      Locating stubs
      Category:Stub categories the main list of stub categories and of articles contained within them
      Category:Stubs deprecated, but still receives a few articles periodically
      Special:Shortpages
      Creating stub types
      Shortcut
      WP:NEWSTUB
      Please propose new stub types at WikiProject Stub sorting/Proposals so that they may be discussed before creating
      them.

      In general, a stub type consists of a stub template and a dedicated stub category, although "upmerged" templates
      are also occasionally created which feed into more general stub categories.

      If you identify a group of stub articles that do not fit an existing stub type, or if an existing stub category is
      growing very large, you can propose the creation of a new stub type which is debated at Wikipedia:WikiProject Stub
      sorting/Proposals.

      Example
      An example of a stub template is , which produces:

      Stub icon
      This website-related article is a stub. You can help Wikipedia by expanding it.

    </div>
  )
}